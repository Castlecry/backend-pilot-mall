package com.pilot.order.service;

import com.pilot.order.dto.ProductResult;

import java.util.List;

public interface ProductService {
    List<ProductResult> listProducts();

    Integer getStock(Long productId);
}
