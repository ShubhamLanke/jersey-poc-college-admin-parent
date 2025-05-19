package com.dss.service;

import com.dss.dto.ProductDTO;
import com.dss.entity.Category;
import com.dss.entity.Category_;
import com.dss.entity.Product;
import com.dss.entity.Product_;
import com.dss.filter.ProductFilterDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private EntityManager entityManager;

//    @Override
//    public List<Product> getProductsSortedByName() {
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
//
//        Root<Product> product = cq.from(Product.class);
//        cq.select(product);
//        cq.orderBy(cb.asc(product.get(Product_.name))); // Type-safe
//
//        return em.createQuery(cq).getResultList();
//    }
//
//    @Override
//    public List<Product> getAllProductsSortedByPrice() {
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
//
//        Root<Product> product = cq.from(Product.class);
//        cq.select(product);
//        cq.orderBy(cb.asc(product.get(Product_.price))); // Type-safe
//
//        return em.createQuery(cq).getResultList();
//    }
//
//    @Override
//    public Product saveProduct(Product product) {
//        em.persist(product);
//        return product;
//    }
//
//    @Override
//    public Product updateProduct(Long id, Product productData) {
//        Product existingProduct = em.find(Product.class, id);
//        if (existingProduct == null) {
//            throw new IllegalArgumentException("Product with id " + id + " not found.");
//        }
//
//        existingProduct.setName(productData.getName());
//        existingProduct.setPrice(productData.getPrice());
//        existingProduct.setAvailable(productData.getAvailable());
//        existingProduct.setCreatedAt(productData.getCreatedAt());
//        existingProduct.setCategory(productData.getCategory());
//
//        em.merge(existingProduct);
//        return existingProduct;
//    }

    @Override
    public List<ProductDTO> findAll(ProductFilterDTO productFilterDTO) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = criteriaBuilder.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();
        if(Strings.isNotEmpty(productFilterDTO.getName())){
            predicates.add(criteriaBuilder.like(root.get(Product_.name),productFilterDTO.getName()));
        }
//        if(Objects.isNull(productFilterDTO.getName())){
//
//        }
        return List.of();
    }

    @Override
    public List<ProductDTO> findAllByPost(ProductFilterDTO productFilterDTO) {
        return List.of();
    }

    @Override
    public List<ProductDTO> getAllProductsSortedByName(ProductFilterDTO filterDTO) {
        return List.of();
    }

    @Override
    public List<ProductDTO> getAllProductsSortedByPrice(ProductFilterDTO filter) {
        return List.of();
    }

    @Override
    public List<ProductDTO> filterProducts(ProductFilterDTO filter) {
        return List.of();
    }

    @Override
    public Response createProduct(ProductDTO productDTO) {
        return null;
    }

    @Override
    public Response updateProduct(Long id, ProductDTO productDTO) {
        return null;
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            em.remove(product);
        }
    }

    public List<Product> filterProducts(String name, String categoryName, Double minPrice, Double maxPrice,
                                        LocalDate startDate, LocalDate endDate, Boolean available, Boolean createdOnWeekend) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> product = cq.from(Product.class);
        Join<Product, Category> category = product.join(Product_.category, JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(cb.lower(product.get(Product_.name)), "%" + name.toLowerCase() + "%"));
        }

        if (categoryName != null && !categoryName.isEmpty()) {
            predicates.add(cb.equal(category.get(Category_.name), categoryName));
        }

        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(product.get(Product_.price), minPrice));
        }

        if (maxPrice != null) {
            predicates.add(cb.lessThanOrEqualTo(product.get(Product_.price), maxPrice));
        }

        if (available != null) {
            predicates.add(cb.equal(product.get(Product_.available), available));
        }

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(product.get(Product_.createdAt), startDate));
        }

        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(product.get(Product_.createdAt), endDate));
        }

        if (createdOnWeekend != null && createdOnWeekend) {
            Expression<Integer> dayOfWeek = cb.function("dayofweek", Integer.class, product.get(Product_.createdAt));
            predicates.add(cb.or(cb.equal(dayOfWeek, 1), cb.equal(dayOfWeek, 7)));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        return em.createQuery(cq).getResultList();
    }
}
