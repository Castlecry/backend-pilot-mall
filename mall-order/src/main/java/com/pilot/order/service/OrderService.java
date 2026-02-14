package com.pilot.order.service;

import com.pilot.order.dto.OrderCreateParam;
import com.pilot.order.dto.OrderListResult;
import com.pilot.order.dto.OrderResult;

import java.util.List;

public interface OrderService {
    OrderResult createOrder(OrderCreateParam param, Long userId);

    List<OrderListResult> listOrders(Long userId);

    boolean checkRepeatOrder(Long userId, Long productId);
}
