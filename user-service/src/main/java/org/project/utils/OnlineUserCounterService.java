package org.project.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OnlineUserCounterService {

    @Resource
    private RedisTemplate<String, Long> redisTemplate;

    private final String ONLINE_USER_COUNT_KEY = "online_user_count";

    /**
     * 增加在线用户
     *
     * @return 增加后的在线用户
     */
    public long incrementOnlineUserCount() {
        // 使用RedisAtomicLong进行原子自增操作
        RedisAtomicLong counter = new RedisAtomicLong(ONLINE_USER_COUNT_KEY, redisTemplate.getConnectionFactory());
        return counter.incrementAndGet();
    }

    /**
     * 获取在线用户
     *
     * @return 当前在线用户
     */
    public long getOnlineUserCount() {
        // 使用RedisAtomicLong获取当前计数值
        RedisAtomicLong counter = new RedisAtomicLong(ONLINE_USER_COUNT_KEY, redisTemplate.getConnectionFactory());
        return counter.get();
    }

    /**
     * 重置在线用户
     */
    public void resetOnlineUserCount() {
        // 将计数器重置为0
        redisTemplate.opsForValue().set(ONLINE_USER_COUNT_KEY, 0L);
    }

}
