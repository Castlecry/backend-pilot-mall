package com.pilot.order.controller;

import com.pilot.common.api.CommonResult;
import com.pilot.order.dto.ProductResult;
import com.pilot.order.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public CommonResult<List<ProductResult>> list() {
        List<ProductResult> products = productService.listProducts();
        return CommonResult.success(products);
    }

    @GetMapping("/stock")
    public CommonResult<Integer> getStock(Long productId) {
        Integer stock = productService.getStock(productId);
        return CommonResult.success(stock);
    }
}
