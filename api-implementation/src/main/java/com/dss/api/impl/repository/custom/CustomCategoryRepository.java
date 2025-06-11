package com.dss.api.impl.repository.custom;

import com.dss.api.impl.entity.Category;
import com.dss.api.impl.filter.ProductFilter;

import java.util.List;

public interface CustomCategoryRepository {

    List<Category> findCategories(ProductFilter filter);

    boolean updateCategory(Long id, String name, String description, Boolean isActive);

    Boolean deleteCategories(List<Long> ids);
}
