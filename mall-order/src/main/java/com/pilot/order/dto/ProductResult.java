package com.pilot.order.dto;

import lombok.Data;

@Data
public class ProductResult {
    private Long id;
    private String name;
    private java.math.BigDecimal price;
    private Integer stock;
    private String description;
}
