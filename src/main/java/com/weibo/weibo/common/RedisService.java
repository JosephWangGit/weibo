package com.weibo.weibo.common;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
public class RedisService {

    private static final String REDIS_LOCK_LUA = "local key = KEYS[1]  local value = ARGV[1]  local ttl = ARGV[2]  if redis.call('setnx', key, value) == 1 then   return redis.call('expire', key, ttl)  else  return 0  end";

    private static final String REDIS_UNLOCK_LUA = "if redis.call('get', KEYS[1]) == ARGV[1] then   return redis.call('del', KEYS[1])  else  return 0  end";

    private static final DefaultRedisScript<Boolean> REDIS_LOCK_SCRIPT = new DefaultRedisScript<>(REDIS_LOCK_LUA, Boolean.class);

    private static final DefaultRedisScript<Boolean> REDIS_UNLOCK_SCRIPT = new DefaultRedisScript<>(REDIS_UNLOCK_LUA, Boolean.class);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public String lock(String key, long expire) {
        String lockId = UUID.randomUUID().toString();
        Boolean isLocked = redisTemplate.execute(REDIS_LOCK_SCRIPT, Collections.singletonList(key), lockId, expire);
        return isLocked != null && isLocked ? lockId : null;

    }

    public Boolean unLock(String key, String lockId) {
        return redisTemplate.execute(REDIS_UNLOCK_SCRIPT, Collections.singletonList(key), lockId);
    }

    public void expire(String key, Long time, TimeUnit timeUnit) {
        if (time > 0) {
            redisTemplate.expire(key, time, timeUnit);
        }
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public void set(String key, Object value, Long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    public boolean setIfAbsent(String key, Object value, Long time, TimeUnit timeUnit) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, time, timeUnit);
    }

    public void scan(String pattern, Consumer<byte[]> consumer) {
        redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(Constant.SCAN_OPTION_COUNT).match(pattern).build())) {
                cursor.forEachRemaining(consumer);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    public String genLikeKey(String prefix, Integer statusId, Integer uid) {
        return prefix
                .concat(Constant.SEPARTOR)
                .concat(String.valueOf(statusId))
                .concat(Constant.SEPARTOR)
                .concat(String.valueOf(uid));
    }
}
