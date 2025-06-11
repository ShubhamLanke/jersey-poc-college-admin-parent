package com.dss.api.impl.repository.custom;

import com.dss.api.impl.entity.Category;
import com.dss.api.impl.entity.Product;
import com.dss.api.impl.filter.ProductFilter;

import java.util.List;

public interface CustomProductRepository {

    List<Product> filterProducts(ProductFilter filter);

    List<Product> findByYear(int year);

    boolean updateProduct(Long id, String name, Double price, String description, Integer quantity, Category category);

    Boolean deleteProducts(List<Long> ids);

    List<Integer> fetchAllYears();
}
