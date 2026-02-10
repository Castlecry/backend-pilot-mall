package com.pilot.order.runner;

import com.pilot.order.domain.PmsProduct;
import com.pilot.order.mapper.PmsProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class StockPreheatRunner implements CommandLineRunner {

    private final PmsProductMapper productMapper;
    private final StringRedisTemplate redisTemplate;

    public StockPreheatRunner(PmsProductMapper productMapper, StringRedisTemplate redisTemplate) {
        this.productMapper = productMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(String... args) {
        log.info("开始库存预热...");
        List<PmsProduct> products = productMapper.selectAll();
        for (PmsProduct product : products) {
            String key = "product:stock:" + product.getId();
            redisTemplate.opsForValue().set(key, String.valueOf(product.getStock()));
            log.info("商品ID: {}, 库存: {}, 已加载到Redis", product.getId(), product.getStock());
        }
        log.info("库存预热完成，共加载 {} 个商品", products.size());
    }
}
