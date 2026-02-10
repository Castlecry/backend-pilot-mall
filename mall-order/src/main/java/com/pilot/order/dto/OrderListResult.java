package com.pilot.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderListResult {
    private Long id;
    private String orderSn;
    private Long userId;
    private Long productId;
    private BigDecimal totalAmount;
    private Integer status;
    private Date createTime;
}
