package com.dss.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductDTO {

    @NotBlank
    private String name;

    private Double price;

    private Boolean available;

    private LocalDate createdAt;

    @NotNull
    private Long categoryId;
}
