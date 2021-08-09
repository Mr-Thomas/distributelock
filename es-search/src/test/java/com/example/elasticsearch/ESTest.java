package com.example.elasticsearch;

import com.example.elasticsearch.pojo.Stu;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author ：Administrator
 * @description：TODO
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
        stu.setStuId(103l);
        stu.setName("张三");
        stu.setAge(18);
        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
        String index = elasticsearchTemplate.index(indexQuery);
        log.info("---{}", index);

        QueryBuilder queryBuilder = QueryBuilders.matchQuery("name", "张三");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(PageRequest.of(0, 10))
                .withSort(SortBuilders.fieldSort("stuId").order(SortOrder.DESC))
                .build();
        AggregatedPage<Stu> stus = elasticsearchTemplate.queryForPage(searchQuery, Stu.class);
        List<Stu> content = stus.getContent();
        log.info(JSONArray.toJSONString(content));
    }
}
