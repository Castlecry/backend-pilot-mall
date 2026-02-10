package com.pilot.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderMessage implements Serializable {
    private String orderSn;
    private Long userId;
    private Long productId;
    private BigDecimal totalAmount;
    private Integer status;
}
