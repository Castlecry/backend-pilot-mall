package com.pilot.order.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OmsOrder {
    private Long id;
    private String orderSn;
    private Long userId;
    private Long productId;
    private BigDecimal totalAmount;
    private Integer status;
    private Date createTime;
}
