package com.toystore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long toyId;
    private String toyName;
    private String imageUrl;
    private Double price;
    private Integer quantity;
    private Double total;
}