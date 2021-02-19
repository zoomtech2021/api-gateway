package com.zhiyong.gateway.dal.resdis;

import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @ClassName RedisMapper
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/9/23 下午3:49
 **/
@Component
public class RedisMapper {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 判断key是否存在
     *
     * @param key
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除key
     *
     * @param key
     */
    public void delKey(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置key过期时间
     *
     * @param key
     */
    public void setExpired(String key, int seconds) {
        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 向缓存写入
     *
     * @param key
     * @param value
     * @param seconds
     */
    public void setValue(String key, Object value, int seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 向缓存写入
     *
     * @param key
     * @param value
     */
    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 向缓存写入
     *
     * @param key
     * @param name
     * @param value
     */
    public void setHashValue(String key, String name, Object value) {
        redisTemplate.opsForHash().put(key, name, value);
    }

    /**
     * 递增缓存hash值
     *
     * @param key
     * @param name
     * @param value
     */
    public void incrHashValue(String key, String name, long value) {
        redisTemplate.opsForHash().increment(key, name, value);
    }

    /**
     * 从缓存获取参数值
     *
     * @param key
     * @return
     */
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 从缓存获取参数值
     *
     * @param key
     * @return
     */
    public Object getHashValue(String key, String name) {
        return redisTemplate.opsForHash().get(key, name);
    }

    /**
     * 发布消息
     *
     * @param channel
     * @param message
     */
    public void publish(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }
}
