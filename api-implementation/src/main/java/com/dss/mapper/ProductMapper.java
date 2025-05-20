package com.dss.mapper;

import com.dss.dto.ProductDTO;
import com.dss.entity.Product;

import java.util.Objects;

public class ProductMapper {

    public static ProductDTO toDTO(Product product) {

        if (Objects.isNull(product)) return null;

        return ProductDTO.builder()
                .name(product.getName())
                .price(product.getPrice())
                .createdAt(product.getCreatedAt())
                .available(product.getAvailable())
                .categoryDTO(CategoryMapper.toDTO(product.getCategory()))
        .build();
    }
}
