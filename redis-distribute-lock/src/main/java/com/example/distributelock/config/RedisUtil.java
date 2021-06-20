package com.example.distributelock.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class RedisUtil {

    /**
     * redis分布式上锁的lua脚本
     */
    private static String LOCK_LUA_SCIPT;

    /**
     * redis分布式释放锁的lua脚本
     */
    private static String UNLOCK_LUA_SCIPT;

    /**
     * 操作成功
     */
    public static final long SUCCESS_RESULT = 1;

    /**
     * 锁已经被释放
     */
    public static final long LOCK_ALREADY_RELEASED = 2;

    /**
     * 释放的不是自己的锁
     */
    public static final long LOCK_NOT_OWN = 3;

    private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    private static RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        RedisUtil.redisTemplate = redisTemplate;
    }

    /**
     * @param channel
     * @param message
     */
    public void convertAndSend(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }

    public Object getObjectFromTopic(Message message) {
        RedisSerializer<?> serializer = redisTemplate.getValueSerializer();
        Object channel = serializer.deserialize(message.getChannel());
        Object messageStr = serializer.deserialize(message.getBody());
        return messageStr;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     */
    public void set(final String key, final Object value, long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value.toString(), time, timeUnit);
    }


    /**
     * 写入hahs缓存
     *
     * @param key
     * @param value
     */
    public void hSet(final String hashkey, final String key, final Object value) {
        redisTemplate.boundHashOps(hashkey).put(key, value);
    }

    /**
     * 写入hash缓存
     * 过期时间 只针对一级key
     *
     * @param key
     * @param value
     */
    public void hSet(final String hashKey, final String key, final Object value, long time, TimeUnit timeUnit) {
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(hashKey);
        ops.put(key, value);
        ops.expire(time, timeUnit);
    }

    public Object hGet(final String hashkey, final String key) {
        return redisTemplate.boundHashOps(hashkey).get(key);
    }

    public Map<Object, Object> hGet(String key) {
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        return map;
    }

    public List hMultiHashGet(final String hashkey, Set keys) {
        return redisTemplate.boundHashOps(hashkey).multiGet(keys);
    }

    public boolean hExists(final String hashkey, final String key) {
        return redisTemplate.boundHashOps(hashkey).hasKey(key);
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     */
    public void set(final String key, final Object value) {
        redisTemplate.opsForValue().set(key, value.toString());
    }


    /**
     * 读取缓存
     *
     * @param key
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final String key, Class<T> clazz) {

        return (T) redisTemplate.boundValueOps(key).get();
    }


    public List<Object> getAllKeyInfo(final String key) {
        // 获取所有的key
        Set<String> keys = redisTemplate.keys(key);
        List<Object> list = this.redisTemplate.opsForValue().multiGet(keys);
        return list;
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        return redisTemplate.boundValueOps(key).get();
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object getObj(final String key) {
        return redisTemplate.boundValueOps(key).get();
    }

    /**
     * 删除，根据key精确匹配
     *
     * @param key
     */
    public void del(final String... key) {
        List<String> list = new ArrayList<>();
        for (String s : key) {
            list.add(s);
        }
        redisTemplate.delete(list);
    }

    public void hDel(final String hashKey, final String... key) {
        // redisTemplate.boundHashOps(hashkey).delete(Arrays.asList(key));
        redisTemplate.boundHashOps(hashKey).delete(key);
    }

    /**
     * 批量删除，根据key模糊匹配
     *
     * @param pattern
     */
    public void delpn(final String... pattern) {
        for (String kp : pattern) {
            redisTemplate.delete(redisTemplate.keys(kp + "*"));
        }
    }

    /**
     * key是否存在
     *
     * @param key
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 从左侧放入list结构
     *
     * @param key
     * @param obj
     * @author:[ljb]
     */
    public void addToList(String key, Object obj) {
        ListOperations opsForList = redisTemplate.opsForList();
        Long size = opsForList.size(key);
        opsForList.leftPush(key, obj);
    }

    /**
     * <p>
     * Description:[取出集合中所有数据]
     * </p>
     *
     * @param key
     * @return
     * @author:[ljb]
     */
    public List<Object> getList(String key) {
        ListOperations opsForList = redisTemplate.opsForList();
        Long size = opsForList.size(key);
        List<Object> range = opsForList.range(key, 0, size - 1);
        return range;
    }

    /**
     * <p>
     * Description:[取出集合中所有数据]
     * </p>
     *
     * @param key
     * @return
     * @author:[ljb]
     */
    public List<Map<String, Object>> getMapList(String key) {
        ListOperations opsForList = redisTemplate.opsForList();
        Long size = opsForList.size(key);
        List<Map<String, Object>> range = opsForList.range(key, 0, size - 1);
        return range;
    }

    /**
     * <p>
     * Description:[取出集合中所有数据]
     * </p>
     *
     * @param key
     * @return
     * @author:[ljb]
     */
    public List<Map<String, Object>> getList1(String key) {
        ListOperations opsForList = redisTemplate.opsForList();
        Long size = opsForList.size(key);
        List<Map<String, Object>> range = opsForList.range(key, 0, size - 1);
        return range;
    }

    /**
     * 从list右侧弹出一个元素
     *
     * @param key
     * @return
     */
    public Object getRightFromList(String key) {
        ListOperations opsForList = redisTemplate.opsForList();
        Object object = opsForList.rightPop(key);
        return object;
    }

    /**
     * 锁的默认超时时间为500毫秒
     *
     * @param key
     */
    public void Lock(String key) {
        String absentKey = key + "_absent";
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(absentKey, "1");
        if (!ifAbsent) {
            int i = 0;
            while (i < 500) {
                if (!exists(absentKey)) {
                    Boolean tryAgain = redisTemplate.opsForValue().setIfAbsent(absentKey, "1");
                    if (tryAgain) {
                        break;
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }
        } else {
            redisTemplate.expire(absentKey, 1000, TimeUnit.MILLISECONDS);
        }

    }

    public void Lock(String key, Long defaultExpire_MILLISECONDS) {
        String absentKey = key + "_absent";
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(absentKey, "1");
        if (!ifAbsent) {
            int i = 0;
            while (i < 500) {
                if (!exists(absentKey)) {
                    Boolean tryAgain = redisTemplate.opsForValue().setIfAbsent(absentKey, "1");
                    if (tryAgain) {
                        break;
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }
        } else {
            redisTemplate.expire(absentKey, defaultExpire_MILLISECONDS, TimeUnit.MILLISECONDS);
        }

    }

    public void UnLock(String key) {
        String absentKey = key + "_absent";
        redisTemplate.delete(absentKey);
    }

    /**
     * 加锁
     *
     * @param key
     * @param value 当前时间+超时时间
     * @return
     */
    public boolean lock(String key, String value) {
        if (redisTemplate.opsForValue().setIfAbsent(key, value)) {
            return true;
        }
        // currentValue=A 这两个线程（X、Y）的value都是B 其中一个线程拿到锁
        String currentValue = (String) redisTemplate.opsForValue().get(key);
        // 如果锁过期
        if (!StringUtils.isEmpty(currentValue) && Long.parseLong(currentValue) < System.currentTimeMillis()) {
            // 获取上一个锁的时间  【getAndSet：获取原来key键对应的值】
            String oldValue = (String) redisTemplate.opsForValue().getAndSet(key, value);
            return !StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue);
        }

        return false;
    }

    /**
     * <p>Description:[获取redis分布式锁]</p>
     *
     * @param: key 锁名称
     * @param: requestId 请求id
     * @param: expireTime 锁的超时时间 毫秒为单位
     * @param: maxWaitTime 获取锁的最大等待时间
     * @return:
     */
    public boolean lock(String key, String requestId, long expireTime, TimeUnit expireTimeUnit, int maxWaitTime, TimeUnit waitTimeUnit) {
        long current = System.currentTimeMillis();
        long lExpireTime = expireTimeUnit.toMillis(expireTime);
        long lWaitTime = waitTimeUnit.toMillis(maxWaitTime);
        while (true) {
            if (tryLock(key, requestId, lExpireTime)) {
                return true;
            }
            if (System.currentTimeMillis() - current > lWaitTime) {
                // 时间到了 还未获取到锁
                return false;
            }
        }
    }


    /**
     * <p>Description:[尝试获取一把锁，其value为requestId，有效期为expireTime]</p>
     *
     * @author:[wangyunliang]
     * @param: key
     * @param: requestId
     * @param: expireTime
     * @return:
     */
    private boolean tryLock(String key, String requestId, long expireTime) {
        List<String> keys = new ArrayList<>(1);
        keys.add(key);
        Long result = (Long) redisTemplate.execute(RedisScript.of(LOCK_LUA_SCIPT, Long.class), keys, requestId, expireTime);
        return result == null ? false : result == SUCCESS_RESULT;
    }

    /**
     * 解锁
     *
     * @param key
     * @param value
     */
    public void unlock(String key, String value) {
        try {
            String currentValue = (String) redisTemplate.opsForValue().get(key);
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                redisTemplate.opsForValue().getOperations().delete(key);
            }
        } catch (Exception e) {
            logger.error("【redis分布式锁】解锁异常, {}", key, e);
        }
    }

    public boolean lock(String key) {
        if (redisTemplate.opsForValue().setIfAbsent(key, "1")) {
            return true;
        }
        return false;
    }

    public void unlock(String key) {
        try {
            redisTemplate.opsForValue().getOperations().delete(key);
        } catch (Exception e) {
            logger.error("【redis分布式锁】解锁异常, {}", key, e);
        }
    }

    /**
     * <p>
     * Description:[判断集合中是否已经存在此值]
     * </p>
     *
     * @param key
     * @param value
     * @return
     * @author:[ljb]
     */
    public boolean isListMember(String key, Object value) {
        List<Object> list = getList(key);
        return list.contains(value);
    }

    public Map<Object, Object> getHashMap(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public Set<Object> getHashMapKeys(String hashkey) {
        return redisTemplate.opsForHash().keys(hashkey);
    }

    public void remove(final String... keys) {
        for (String key : keys) {
            redisTemplate.delete(key);
        }
    }

    public void setHashKey(final String hashKey, Object var2, Object var3) {
        if (var2 instanceof String) {
            redisTemplate.opsForHash().put(hashKey, var2, var3);
        } else {
            redisTemplate.opsForHash().put(hashKey, var2.toString(), var3);
        }
    }

    /**
     * <p>Discription:[存储到redis，并设置过期时间，过期时间单位：秒]</p>
     *
     * @param key
     * @param value
     * @param expireTime (秒)
     * @return
     * @author:[wengxiongfei]
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
        }
        return result;
    }

    public void setHashMap(String key, Map map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 验证指定 key 下 有没有指定的 hashkey
     *
     * @param key
     * @param hashKey
     * @return
     */
    public boolean hashKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }


    /**
     * 获取指定 key 下的 hashkey
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Object getHashKey(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    public Long increment(String key) {
        Long l = redisTemplate.boundValueOps(key).increment(1);
        if (l != null) {
            return l;
        }
        return -1L;
    }

    public Long increment(String key, long liveTime, long delta) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long increment = entityIdCounter.addAndGet(delta);
        // 初始设置过期时间
        if (liveTime > 0) {
            entityIdCounter.expire(liveTime, TimeUnit.SECONDS);
        }

        return increment;
    }

    public Long increment(String key, long liveTime, long delta, TimeUnit timeUnit) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long increment = entityIdCounter.addAndGet(delta);
        // 初始设置过期时间
        if (liveTime > 0) {
            entityIdCounter.expire(liveTime, timeUnit);
        }
        return increment;
    }

    /**
     * 查询key的生命周期
     *
     * @param key
     * @return
     */
    public long getKeyExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * <p>Discription:[批量插入key-value的缓存，不建议使用，集群环境不支持]</p>
     *
     * @author:[xjx]
     * @param: map
     * @param: suffixKey key后缀
     */
    @Deprecated
    public void batchSet(Map<String, Integer> map, String suffixKey) {

        redisTemplate.executePipelined(new RedisCallback<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> stringSerializer = new StringRedisSerializer();
                for (String key : map.keySet()) {
                    String redisKey = key + suffixKey;
                    byte[] rawKey = stringSerializer.serialize(redisKey);
                    connection.setNX(rawKey, stringSerializer.serialize(map.get(key).toString()));
                }
                return null;
            }
        });
    }

    public long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public void setForIncrement(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void addZSet(final String zsetKey, long l) {
        redisTemplate.opsForZSet().add(zsetKey, l, l);
    }

    /**
     * 添加 ZSet 元素
     * Zset 根据 socre 排序   不重复 每个元素附加一个 socre  double类型的属性(double 可以重复)
     *
     * @param key
     * @param value
     * @param score
     */
    public boolean add(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 获取指定成员的 score 值
     *
     * @param key
     * @param value
     * @return
     */
    public Double score(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 获取分数范围内的 [min,max] 的排序结果集合 (从小到大,只有列名)
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<Object> rangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 删除指定 分数范围 内的成员 [main,max],其中成员分数按( 从小到大 )
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long removeRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    public long getZSetSize(final String zsetKey) {
        return redisTemplate.opsForZSet().size(zsetKey);
    }

    public Set<Object> getZSetByRange(final String zsetKey, int start, int end) {
        return redisTemplate.opsForZSet().range(zsetKey, start, end);
    }

    public Long removeZSet(final String zsetKey, int start, int end) {
        return redisTemplate.opsForZSet().removeRange(zsetKey, start, end);
    }

    public long hashIncrement(final String hashKey, Object o) {
        return redisTemplate.opsForHash().increment(hashKey, o.toString(), 1);
    }

    public long hashIncrement(final String hashKey, Object o, int size) {
        return redisTemplate.opsForHash().increment(hashKey, o.toString(), size);
    }

    public void deleteHashKey(String h, Object o) {
        redisTemplate.opsForHash().delete(h, o);
    }

    /**
     * Created on 2019年05月15日 14:12:59
     * <p>Description:[检查redis的状态]</p>
     *
     * @author:[wangyunliang]
     * @return: true-redis连接ok，flase-redis连接有异常
     */
    public boolean checkRedisStatus() {
        final String key = "monitorKey";
        final String value = toFullDate(new Date());
        set(key, value);
        if (!value.equals(get(key))) {
            return false;
        }
        del(key);
        return true;
    }

    private static String toFullDate(Date date) {
        return toString(date, "yyyy-MM-dd HH:mm:ss");
    }

    private static String toString(Date date, String format) {
        return (new SimpleDateFormat(format)).format(date);
    }

    /**
     * @param key      不存在返回true；存在返回false
     * @param value
     * @param time
     * @param timeUnit
     */
    public void setIfAbsent(final String key, final Object value, long time, TimeUnit timeUnit) {
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, value);
        if (flag) {
            redisTemplate.expire(key, time, timeUnit);
        }
    }

    /**
     * @param key      不存在返回true,即成功获取锁，同时将key存进去；存在返回false
     * @param value
     * @param time
     * @param timeUnit
     * @return
     */
    public Boolean setSyncIfAbsent(final String key, final Object value, long time, TimeUnit timeUnit) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, time, timeUnit);
    }

    /**
     * 采用lua脚本校验随机数，释放锁
     * @param key
     * @param requestValue
     * @return
     */
    public Boolean unLock(String key, String requestValue) {
        //lua脚本【释放锁时校验之前设置的随机数，相同才能释放】
        String script = "if redis.call(\"get\",KEYS[1])==ARGV[1] then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end";
        RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);
        List<String> keys = Arrays.asList(key);
        Boolean result = redisTemplate.execute(redisScript, keys, requestValue);
        return result;
    }

    public List<Object> getHashMapValues(String hashkey) {
        return redisTemplate.opsForHash().values(hashkey);
    }
}
