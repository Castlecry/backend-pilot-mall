package com.pilot.order.service.impl;

import com.pilot.order.domain.PmsProduct;
import com.pilot.order.dto.ProductResult;
import com.pilot.order.mapper.PmsProductMapper;
import com.pilot.order.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private PmsProductMapper productMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public List<ProductResult> listProducts() {
        List<PmsProduct> products = productMapper.selectAll();
        return products.stream().map(this::convertToResult).collect(Collectors.toList());
    }

    @Override
    public Integer getStock(Long productId) {
        String key = "product:stock:" + productId;
        String stockStr = redisTemplate.opsForValue().get(key);
        if (stockStr == null) {
            PmsProduct product = productMapper.selectById(productId);
            if (product == null) {
                return 0;
            }
            return product.getStock();
        }
        return Integer.parseInt(stockStr);
    }

    private ProductResult convertToResult(PmsProduct product) {
        ProductResult result = new ProductResult();
        result.setId(product.getId());
        result.setName(product.getName());
        result.setPrice(product.getPrice());
        result.setStock(product.getStock());
        result.setDescription(product.getDescription());
        return result;
    }
}
