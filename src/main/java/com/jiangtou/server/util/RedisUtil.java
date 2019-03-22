package com.jiangtou.server.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * @author 陆迪
 * @date 2018-10-12 21:29
 **/
@Component
public class RedisUtil {

    private static Logger logger = LogManager.getFormatterLogger(RedisUtil.class.getName());


    private static RedisTemplate<String, Object> redisTemplate;


    @Resource
    private void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        logger.debug("初始化RedisUtil");
        RedisUtil.redisTemplate = redisTemplate;
    }

    /**
     * 防止返回空值
     *
     * @param key
     * @return 若取得null值， 则返回true
     */
    public static boolean hasKey(String key) {
        if (StringUtil.isEmpty(key)) {
            return false;
        }
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey == null) {
            return false;
        } else {
            return hasKey;
        }
    }

    public static boolean unHasKey(String key) {
        return !RedisUtil.hasKey(key);
    }

    /**
     * 旧的数据全部删除
     * @param key
     * @param result
     * @param timeout
     * @param unit
     * @param <K>
     * @param <V>
     */
    @SuppressWarnings("unchecked")
    public static <K, V> void replaceMapIntoRedisHash(
            final String key, final Map<K, V> result, final long timeout, @NotNull final TimeUnit unit) {
        logger.debug("replaceMapIntoRedisHash" + key);
        if (StringUtil.isEmpty(key) || result == null || result.isEmpty()) {
            return;
        }

        //先删后插入
        if (hasKey(key)) {
            redisTemplate.setEnableTransactionSupport(true);
            redisTemplate.multi();
            redisTemplate.delete(key);
            HashOperations<String, K, V> hashOperations = redisTemplate.opsForHash();
            hashOperations.putAll(key, result);
            redisTemplate.exec();
            redisTemplate.setEnableTransactionSupport(false);
        } else {
            HashOperations<String, K, V> hashOperations = redisTemplate.opsForHash();
            hashOperations.putAll(key, result);
        }

        redisTemplate.expire(key, timeout, unit);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> void updateMapIntoRedisHash(
            final String key, final Map<K, V> result, final long timeout, @NotNull final TimeUnit unit) {
        logger.debug("replaceMapIntoRedisHash" + key);
        if (StringUtil.isEmpty(key) || result == null || result.isEmpty()) {
            return;
        }

        HashOperations<String, K, V> hashOperations = redisTemplate.opsForHash();
        //先删后插入
        if (hasKey(key)) {
            redisTemplate.setEnableTransactionSupport(true);
            redisTemplate.multi();
            for (Map.Entry<K, V> entry : result.entrySet()) {
                K hashKey = entry.getKey();
                hashOperations.put(key, hashKey, entry.getValue());
            }
            redisTemplate.exec();
            redisTemplate.setEnableTransactionSupport(false);
        }

        redisTemplate.expire(key, timeout, unit);
    }



    @SuppressWarnings("unchecked")
    public static <K, V> void insertValueIntoRedisHash(
            final String key, final K hashKey, final V value, final long timeout, final TimeUnit unit) {
        RedisUtil.insertValueIntoRedisHash(key, hashKey, value);
       redisTemplate.expire(key, timeout, unit);

    }

    public static <K, V> void insertValueIntoRedisHash(
            final String key, final K hashKey, final V value) {
        if (StringUtil.isEmpty(key) || hashKey == null || value == null) {
            return;
        }
        HashOperations<String, K, V> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(key, hashKey, value);
    }


    /**获取hashKey 的所有值 */
    public static <K, V> Map<K, V> getHashMap(String key) {
        if (StringUtil.isEmpty(key)) {
            return new TreeMap<>();
        }

        Map<K, V> resultMap;
        HashOperations<String, K, V> hashOperations = redisTemplate.opsForHash();
        resultMap = hashOperations.entries(key);

        return resultMap;
    }
    /**获取hashKey 的某个key对应的值 */
    public static <K, V> Optional<V> getHashValue(String key, K hashKey) {
        if (StringUtil.isEmpty(key) || hashKey == null) {
            return Optional.empty();
        }

        V value = null;
        HashOperations<String, K, V> hashOperations = redisTemplate.opsForHash();

        if (hashOperations.hasKey(key, hashKey)) {
            value = hashOperations.get(key, hashKey);
        }

        return Optional.ofNullable(value);
    }


    @SuppressWarnings("unchecked")
    public static <V> void insertValueIntoRedisValue(final String key, final V result, final long timeout, final TimeUnit unit) {
        if (StringUtil.isEmpty(key) || result == null) {
            return;
        }

        ValueOperations<String, V> valueOperations = (ValueOperations<String, V>) redisTemplate.opsForValue();
        valueOperations.set(key, result, timeout, unit);

    }

    @SuppressWarnings("unchecked")
    public static <V> Optional<V> getValue(String valueKey) {
        if (StringUtil.isEmpty(valueKey)) {
            return Optional.empty();
        }
        if (RedisUtil.hasKey(valueKey)) {
            ValueOperations<String, V> valueOperations = (ValueOperations<String, V>) redisTemplate.opsForValue();
            V value = valueOperations.get(valueKey);
            return Optional.ofNullable(value);
        }

        return Optional.empty();
    }

    public static void deleteKey(String key) {
        redisTemplate.delete(key);
    }





}
