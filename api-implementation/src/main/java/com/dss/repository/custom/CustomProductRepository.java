package com.dss.repository.custom;

import com.dss.entity.Category;
import com.dss.entity.Product;
import com.dss.filter.ProductFilter;

import java.util.List;

public interface CustomProductRepository {

    List<Product> filterProducts(ProductFilter filter);

    List<Product> findByYear(int year);

    boolean updateProduct(Long id, String name, Double price, String description, Integer quantity, Category category);

    void deleteProduct(Long id);
}
