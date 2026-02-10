package com.pilot.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderResult {
    private String orderSn;
    private Long productId;
    private BigDecimal totalAmount;
    private Integer status;
}
