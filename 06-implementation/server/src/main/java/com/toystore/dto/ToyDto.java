package com.toystore.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ToyDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Integer stock;
    private String category;
}