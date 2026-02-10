package com.pilot.order.controller;

import com.pilot.common.api.CommonResult;
import com.pilot.order.dto.OrderCreateParam;
import com.pilot.order.dto.OrderListResult;
import com.pilot.order.dto.OrderResult;
import com.pilot.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public CommonResult<OrderResult> createOrder(@RequestBody OrderCreateParam param,
                                                   @RequestHeader("X-User-Id") Long userId) {
        OrderResult result = orderService.createOrder(param, userId);
        return CommonResult.success(result);
    }

    @GetMapping("/list")
    public CommonResult<List<OrderListResult>> listOrders(@RequestHeader("X-User-Id") Long userId) {
        List<OrderListResult> result = orderService.listOrders(userId);
        return CommonResult.success(result);
    }
}
