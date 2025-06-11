package com.dss.api.impl.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CategoryDto {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;
    private String description;
    private Boolean isActive;
    private List<ProductDto> products;

}
