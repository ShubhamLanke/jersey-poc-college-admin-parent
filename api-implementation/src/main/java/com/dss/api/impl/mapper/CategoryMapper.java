package com.dss.api.impl.mapper;

import com.dss.api.impl.dto.CategoryDto;
import com.dss.api.impl.entity.Category;
import com.dss.api.impl.entity.Product;

import java.util.stream.Collectors;

public class CategoryMapper {

    public static CategoryDto toDto(Category category) {
        return toDto(category, false);
    }

    public static CategoryDto toDto(Category category, boolean includeProducts) {
        if (category == null) {
            return null;
        }

        CategoryDto dto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .build();

        if (includeProducts && category.getProducts() != null) {
            dto.setProducts(
                    category.getProducts()
                            .stream()
                            .map(product -> ProductMapper.toDto(product, false))
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public static Category toEntity(CategoryDto dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setIsActive(dto.getIsActive());

        if (dto.getProducts() != null) {
            category.setProducts(
                    dto.getProducts()
                            .stream()
                            .map(productDto -> {
                                Product product = ProductMapper.toEntity(productDto, false);
                                product.setCategory(category);
                                return product;
                            })
                            .collect(Collectors.toList())
            );
        }

        return category;
    }
}
