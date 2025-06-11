package com.dss.api.impl.service;

import com.dss.api.impl.dto.CategoryDto;
import com.dss.api.impl.entity.Category;
import com.dss.api.impl.filter.ProductFilter;
import com.dss.api.impl.mapper.CategoryMapper;
import com.dss.api.impl.repository.CategoryRepository;
import com.dss.api.impl.repository.custom.CustomCategoryRepository;
import jakarta.annotation.Resource;
import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Resource
    private CategoryRepository categoryRepository;
    @Resource
    private CustomCategoryRepository customCategoryRepository;

    @Override
    public List<CategoryDto> fetchAll() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(entity -> CategoryMapper.toDto(entity, Boolean.TRUE)).toList();
    }

    @Override
    public List<CategoryDto> fetchAllCategory(ProductFilter filter) {
        List<Category> categories = customCategoryRepository.findCategories(filter);
        return categories.stream()
                .map(entity -> CategoryMapper.toDto(entity, Boolean.TRUE)).toList();
    }

    @Override
    public Response createCategory(CategoryDto categoryDto) {
        return null;
    }

    @Override
    public Response updateCategory(CategoryDto categoryDto) {
        return null;
    }

    @Override
    public Response saveAndUpdateCategory(CategoryDto categoryDto) {
        if (categoryDto.getId() == null) {
            Category category = CategoryMapper.toEntity(categoryDto);
            categoryRepository.save(category);
            return Response.status(Response.Status.CREATED).entity("Category created successfully").build();
        } else {
            boolean updated = customCategoryRepository.updateCategory(
                    categoryDto.getId(),
                    categoryDto.getName(),
                    categoryDto.getDescription(),
                    categoryDto.getIsActive()
            );

            if (updated) {
                return Response.ok().entity("Category updated successfully").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Category not found with ID: " + categoryDto.getId()).build();
            }
        }
    }

    @Override
    public Boolean deleteCategory(List<Long> ids) {
        return customCategoryRepository.deleteCategories(ids);
    }
}
