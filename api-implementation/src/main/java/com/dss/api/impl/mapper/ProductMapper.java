package com.dss.api.impl.mapper;

import com.dss.api.impl.dto.ProductDto;
import com.dss.api.impl.entity.Product;

public class ProductMapper {

    public static ProductDto toDto(Product product) {
        return toDto(product, true);
    }

    public static ProductDto toDto(Product product, boolean includeCategory) {
        if (product == null) return null;

        ProductDto dto = ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .quantity(product.getQuantity())
                .createdOn(product.getCreatedOn())
                .isActive(product.getIsActive())
                .build();

        if (includeCategory) {
            dto.setCategoryDto(CategoryMapper.toDto(product.getCategory(), false)); // shallow
        }

        return dto;
    }

    public static Product toEntity(ProductDto dto) {
        return toEntity(dto, true);
    }

    public static Product toEntity(ProductDto dto, boolean includeCategory) {
        if (dto == null) return null;

        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        product.setQuantity(dto.getQuantity());
        product.setCreatedOn(dto.getCreatedOn());
        product.setIsActive(dto.getIsActive());

        if (includeCategory) {
            product.setCategory(CategoryMapper.toEntity(dto.getCategoryDto()));
        }

        return product;
    }
}
