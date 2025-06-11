package com.dss.api.impl.repository.impl;

import com.dss.api.impl.dto.CategoryDto;
import com.dss.api.impl.entity.Category;
import com.dss.api.impl.entity.Category_;
import com.dss.api.impl.entity.Product;
import com.dss.api.impl.entity.Product_;
import com.dss.api.impl.filter.ProductFilter;
import com.dss.api.impl.repository.custom.CustomCategoryRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomCategoryRepositoryImpl implements CustomCategoryRepository {

    @Resource
    private EntityManager entityManager;

    @Override
    public List<Category> findCategories(ProductFilter filter) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Category> query = builder.createQuery(Category.class);
        Root<Category> root = query.from(Category.class);
        Join<Category, Product> productJoin = root.join(Category_.PRODUCTS);

        List<Predicate> predicates = buildPredicates(filter, builder, productJoin);

        if (predicates.isEmpty()) {
            return List.of();
        }

        query.select(root).distinct(true)
                .where(builder.and(predicates.toArray(new Predicate[0])))
                .orderBy(builder.asc(root.get(Category_.NAME)));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    @Transactional
    public boolean updateCategory(Long id, String name, String description, Boolean isActive) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Category> update = cb.createCriteriaUpdate(Category.class);
        Root<Category> root = update.from(Category.class);

        if (name != null) {
            update.set(Category_.name, name);
        }
        if (description != null) {
            update.set(Category_.description, description);
        }
        if (isActive != null) {
            update.set(Category_.isActive, isActive);
        }

        update.where(cb.equal(root.get(Category_.id), id));

        int updatedCount = entityManager.createQuery(update).executeUpdate();

        return updatedCount > 0;
    }

    @Override
    public Boolean deleteCategories(List<Long> ids) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<Category> delete = cb.createCriteriaDelete(Category.class);
        Root<Category> root = delete.from(Category.class);
        Predicate predicate = root.get(Category_.ID).in(ids);
        delete.where(predicate);
        int result = entityManager.createQuery(delete).executeUpdate();
        return result > 0;
    }

    private List<Predicate> buildPredicates(ProductFilter filter, CriteriaBuilder builder, Join<Category, Product> productJoin) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getProductName() != null && !filter.getProductName().isEmpty()) {
            List<Predicate> namePredicates = filter.getProductName().stream()
                    .filter(StringUtils::isNotBlank)
                    .map(name ->
                            builder.like(
                                    builder.lower(productJoin.get(Product_.name)),
                                    "%" + name.toLowerCase() + "%"
                            )
                    )
                    .collect(Collectors.toList());

            predicates.add(builder.or(namePredicates.toArray(new Predicate[0])));
        }


        if (filter.getYear() != null && !filter.getYear().isEmpty()) {
            filter.getYear().sort(Comparator.comparingInt(value -> value));
            LocalDate startDate = LocalDate.of(filter.getYear().getFirst(), 1, 1);
            LocalDate endDate = LocalDate.of(filter.getYear().getLast(), 12,31);
            Predicate between = builder.between(productJoin.get(Product_.createdOn), startDate, endDate);

//            Expression<Integer> yearExpr = builder.function("YEAR", Integer.class, productJoin.get(Product_.CREATED_ON));
            predicates.add(between);
        }

        if (filter.getCategoryDto() != null && !filter.getCategoryDto().isEmpty()) {
            List<Long> categoryIds = filter.getCategoryDto().stream()
                    .map(CategoryDto::getId)
                    .toList();
            predicates.add(productJoin.get(Product_.CATEGORY).get(Category_.ID).in(categoryIds));
        }

        return predicates;
    }
}
