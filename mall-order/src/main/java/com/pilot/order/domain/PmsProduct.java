package com.pilot.order.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PmsProduct {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private Date createTime;
}
