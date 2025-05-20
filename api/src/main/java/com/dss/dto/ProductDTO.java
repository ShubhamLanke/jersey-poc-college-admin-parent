package com.dss.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

    private Double price;

    private Boolean available;

    private LocalDate createdAt;

    @NotNull
    private CategoryDTO categoryDTO;
}
