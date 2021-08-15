package com.example.elasticsearch;

import com.alibaba.fastjson.JSONObject;
import com.example.elasticsearch.pojo.Stu;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @author ：Administrator
 * @description：不建议使用 ElasticsearchTemplate 对索引进行管理（创建、更新、删除索引）
 * @date ：2021/8/8 21:38
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticsearchApplication.class)
public class ESTest {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 创建索引并插入数据
     */
    @Test
    public void createIndex() {
        Stu stu = new Stu();
        stu.setStuId(105l);
        stu.setName("张四");
        stu.setAge(18);
        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
        String index = elasticsearchTemplate.index(indexQuery);
        log.info("---{}", index);
    }

    /**
     * 删除索引
     */
    @Test
    public void delIndex() {
//        elasticsearchTemplate.deleteIndex(Stu.class);
        elasticsearchTemplate.deleteIndex("stu");
    }

    /**
     * 修改
     */
    @Test
    public void updateStuDoc() {
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("sign", "haha");
            put("money", 88.6f);
            put("age", 33);
        }};
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(map);
        UpdateQuery updateQuery = new UpdateQueryBuilder()
                .withClass(Stu.class)
                .withId("103")
                .withIndexRequest(indexRequest)
                .build();
//      update stu set sign='abc',age=33 where docId = '103';
        elasticsearchTemplate.update(updateQuery);
    }

    @Test
    public void queryStuDoc() {
        GetQuery getQuery = new GetQuery();
        getQuery.setId("103");
        Stu stu = elasticsearchTemplate.queryForObject(getQuery, Stu.class);
        log.info("GetQuery:{}", JSONObject.toJSONString(stu));
    }

    @Test
    public void delStuDoc() {
        elasticsearchTemplate.delete(Stu.class, "103");
    }

    @Test
    public void searchStuDoc() {
        /**
         * 设置的高亮映射
         */
        SearchResultMapper searchResultMapper = new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<Stu> list = new ArrayList<>();
                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits) {
                    HighlightField highlightField = hit.getHighlightFields().get("name");
                    String name = highlightField.getFragments()[0].toString();//获取高亮字段
                    Stu stu = new Stu();//其他字段也得返回
                    stu.setStuId(Long.valueOf(hit.getSourceAsMap().get("stuId").toString()));
                    stu.setName(name);
                    stu.setAge(ObjectUtils.isNotEmpty(hit.getSourceAsMap().get("age")) ? Integer.parseInt(hit.getSourceAsMap().get("age").toString()) : 0);
                    stu.setMoney(ObjectUtils.isNotEmpty(hit.getSourceAsMap().get("money")) ? Float.parseFloat(hit.getSourceAsMap().get("money").toString()) : 0F);
                    stu.setSign(hit.getSourceAsMap().get("sing") + "");
                    stu.setDescription(hit.getSourceAsMap().get("description") + "");
                    list.add(stu);
                }
                return (AggregatedPage<T>) new AggregatedPageImpl<>(list, pageable, hits.getTotalHits());
            }

            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                return null;
            }
        };

        QueryBuilder queryBuilder = QueryBuilders.matchQuery("name", "张");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                //分页
                .withPageable(PageRequest.of(0, 10))
                //高亮
                .withHighlightFields(new HighlightBuilder.Field("name")
                        .preTags("<font color = 'red'>")
                        .postTags("</font>"))
                //排序
                .withSort(SortBuilders.fieldSort("stuId").order(SortOrder.DESC))
                .build();
        AggregatedPage<Stu> stus = elasticsearchTemplate.queryForPage(searchQuery, Stu.class, searchResultMapper);
        List<Stu> content = stus.getContent();
        log.info("总分页数：{}", stus.getTotalPages());
        log.info("文档数据：{}", JSONArray.toJSONString(content));
    }

}
