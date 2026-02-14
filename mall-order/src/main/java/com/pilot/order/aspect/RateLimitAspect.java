package com.pilot.order.aspect;

import com.pilot.order.annotation.RateLimit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        Long userId = getUserId();
        String key = "rate:limit:seckill:" + userId;

        //StringRedisTemplate 获取的是 String，需要手动转换为 Integer
        String value = redisTemplate.opsForValue().get(key);
        Integer count = (value != null) ? Integer.parseInt(value) : null;

        if (count == null) {
            //set 的值必须是 String 类型，所以这里传入字符串 "1"
            redisTemplate.opsForValue().set(key, "1", rateLimit.timeout(), TimeUnit.SECONDS);
        } else if (count >= rateLimit.limit()) {
            log.warn("用户{}请求太频繁，超过限制", userId);
            throw new RuntimeException("请求太频繁，请稍后重试");
        } else {
            // increment 操作 Redis 会自动处理数值型字符串
            redisTemplate.opsForValue().increment(key);
        }

        return joinPoint.proceed();
    }

    private Long getUserId() {
        return 1L;
    }
}