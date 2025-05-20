package com.dss.mapper;

import com.dss.dto.CategoryDTO;
import com.dss.entity.Category;

public class CategoryMapper {

    public static CategoryDTO toDTO(Category category) {
        if (category == null) return null;

        return CategoryDTO.builder()
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}



