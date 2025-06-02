package com.dss.repository.impl;

import com.dss.entity.Category;
import com.dss.entity.Category_;
import com.dss.entity.Product;
import com.dss.entity.Product_;
import com.dss.filter.ProductFilter;
import com.dss.repository.custom.CustomProductRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class CustomProductRepositoryImpl implements CustomProductRepository {

    @Resource
    private EntityManager entityManager;

    public List<Product> filterProducts(ProductFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);
        Join<Product, Category> categoryJoin = root.join(Product_.category);

        List<Predicate> predicates = new ArrayList<>();

        // Filter by product name (IN clause)
        if (filter.getProductName() != null && !filter.getProductName().isEmpty()) {
            predicates.add(root.get(Product_.name).in(filter.getProductName()));
        }

        // Filter by category name (IN clause)
        if (filter.getCategoryName() != null && !filter.getCategoryName().isEmpty()) {
            predicates.add(categoryJoin.get(Category_.name).in(filter.getCategoryName()));
        }

        // Filter by year using function
        if (filter.getYear() != null && !filter.getYear().isEmpty()) {
            Expression<Integer> yearExpr = cb.function("YEAR", Integer.class, root.get(Product_.createdOn));
            predicates.add(yearExpr.in(filter.getYear()));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getResultList();
    }

    public List<Product> findByYear(int year) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);
        Expression<Integer> yearExpr = cb.function("YEAR", Integer.class, root.get(Product_.createdOn));
        query.where(cb.equal(yearExpr, year));
        return entityManager.createQuery(query).getResultList();
    }

    @Transactional
    public boolean updateProduct(Long id, String name, Double price, String description, Integer quantity, Category category) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Product> update = cb.createCriteriaUpdate(Product.class);
        Root<Product> root = update.from(Product.class);

        update.set(Product_.name, name);
        update.set(Product_.price, price);
        update.set(Product_.description, description);
        update.set(Product_.quantity, quantity);
        update.set(Product_.category, category);

        update.where(cb.equal(root.get(Product_.id), id));
        return entityManager.createQuery(update).executeUpdate() > 0;
    }

    @Transactional
    public void deleteProduct(Long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<Product> delete = cb.createCriteriaDelete(Product.class);
        Root<Product> root = delete.from(Product.class);
        delete.where(cb.equal(root.get(Product_.id), id));
        entityManager.createQuery(delete).executeUpdate();
    }
}
