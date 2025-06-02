package com.dss.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    private Long id;
    private String name;
    private Double price;
    private LocalDate createdOn;
    private String description;
    private Integer quantity;
    private CategoryDto categoryDto;
}
