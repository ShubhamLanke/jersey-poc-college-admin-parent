package com.dss.api.impl.service;

import com.dss.api.impl.dto.ProductDto;
import com.dss.api.impl.entity.Category;
import com.dss.api.impl.entity.Product;
import com.dss.api.impl.filter.ProductFilter;
import com.dss.api.impl.mapper.ProductMapper;
import com.dss.api.impl.repository.CategoryRepository;
import com.dss.api.impl.repository.ProductRepository;
import com.dss.api.impl.repository.custom.CustomProductRepository;
import jakarta.annotation.Resource;
import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductRepository productRepository;
    @Resource
    private CategoryRepository categoryRepository;
    @Resource
    private CustomProductRepository customProductRepository;

//    @Override
//    public List<ProductDto> findAll(String name) {
//        List<Product> products;
//        if (name != null && !name.isEmpty()) {
//            products = productRepository.findByNameContainingIgnoreCase(name);
//        } else {
//            products = productRepository.findAll();
//        }        return products.stream()
//                .map(ProductMapper::toDto)
//                .collect(Collectors.toList());
//    }

    @Override
    public List<ProductDto> findByYear(Integer year) {
        if (year == null) {
            return List.of();
        }

        return customProductRepository.findByYear(year)
                .stream()
                .map(entity -> ProductMapper.toDto(entity, Boolean.TRUE))
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> fetchAllYears() {
        return customProductRepository.fetchAllYears();
    }

    @Override
    public List<ProductDto> filterProducts(ProductFilter productFilter) {
        return customProductRepository.filterProducts(productFilter)
                .stream()
                .map(entity -> ProductMapper.toDto(entity, Boolean.TRUE))
                .collect(Collectors.toList());
    }

    @Override
    public Response createProduct(ProductDto productDTO) {
        return null;
    }

    @Override
    public Response updateProduct(ProductDto productDTO) {
        return null;
    }

    //    @Override
//    public Response createProduct(ProductDto productDTO) {
//        Category category = categoryRepository.findById(productDTO.getCategoryDto().getId()).orElse(null);
//        if (category == null) {
//            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid category").build();
//        }
//        Product product = ProductMapper.toEntity(productDTO);
//        productRepository.save(product);
//        return Response.status(Response.Status.CREATED).build();
//    }
//
//    @Override
//    public Response updateProduct(ProductDto productDTO) {
//        Category category = categoryRepository.findById(productDTO.getCategoryDto().getId()).orElse(null);
//        if (category == null) {
//            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid category").build();
//        }
//
//        boolean updated = customProductRepository.updateProduct(
//                productDTO.getId(),
//                productDTO.getName(),
//                productDTO.getPrice(),
//                productDTO.getDescription(),
//                productDTO.getQuantity(),
//                category
//        );
//
//        if (updated) {
//            return Response.ok().entity("Product updated successfully").build();
//        } else {
//            return Response.status(Response.Status.NOT_FOUND).entity("Product not found").build();
//        }
//    }

    @Override
    public Response saveAndUpdateProduct(ProductDto productDTO) {
        Category category = categoryRepository.findById(productDTO.getCategoryDto().getId()).orElse(null);
        if (category == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid category").build();
        }

        if (productDTO.getId() == null) {
            Product product = ProductMapper.toEntity(productDTO);
            product.setCategory(category);
            productRepository.save(product);
            return Response.status(Response.Status.CREATED).entity("Product created successfully").build();
        } else {

            boolean updated = customProductRepository.updateProduct(
                    productDTO.getId(),
                    productDTO.getName(),
                    productDTO.getPrice(),
                    productDTO.getDescription(),
                    productDTO.getQuantity(),
                    category
            );

            if (updated) {
                return Response.ok().entity("Product updated successfully").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Product not found with ID: " + productDTO.getId()).build();
            }
        }
    }

    @Override
    public Boolean deleteProducts(List<Long> ids) {
        return customProductRepository.deleteProducts(ids);
    }
}
