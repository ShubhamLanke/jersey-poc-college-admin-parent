package com.dss.api.impl.repository.impl;

import com.dss.api.impl.dto.CategoryDto;
import com.dss.api.impl.entity.Category;
import com.dss.api.impl.entity.Category_;
import com.dss.api.impl.entity.Product;
import com.dss.api.impl.entity.Product_;
import com.dss.api.impl.filter.ProductFilter;
import com.dss.api.impl.repository.custom.CustomProductRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class CustomProductRepositoryImpl implements CustomProductRepository {

    @Resource
    private EntityManager entityManager;

    public List<Product> filterProducts(ProductFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(filter)) {
            if (!CollectionUtils.isEmpty(filter.getProductName())) {
                predicates.add(root.get(Product_.name).in(filter.getProductName()));
            }

            if (filter.getCategoryDto() != null && !filter.getCategoryDto().isEmpty()) { // use List<Long> for Category Ids in filter
                List<String> names =  filter.getCategoryDto().stream()
                        .map(CategoryDto::getName)
                        .collect(Collectors.toList());
                final Join<Product, Category> categoryJoin = root.join(Product_.category, JoinType.INNER);
//                root.get(Product_.CATEGORY).get(Category_.ID).in(filter.getYear())
                Predicate categoryNamePredicate = categoryJoin.get("name").in(names);
                predicates.add(categoryNamePredicate);
            }

            if (filter.getYear() != null && !filter.getYear().isEmpty()) {
                filter.getYear().sort(Comparator.comparingInt(value -> value));
                LocalDate startDate = LocalDate.of(filter.getYear().getFirst(), 1, 1);
                LocalDate endDate = LocalDate.of(filter.getYear().getLast(), 12,31);
                Predicate between = cb.between(root.get(Product_.createdOn), startDate, endDate);
//                Expression<Integer> yearExpr = cb.function("YEAR", Integer.class, root.get(Product_.createdOn));
                predicates.add(between);
            }
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
    public Boolean deleteProducts(List<Long> ids) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<Product> delete = cb.createCriteriaDelete(Product.class);
        Root<Product> root = delete.from(Product.class);
        Predicate predicate = root.get(Product_.ID).in(ids);
        delete.where(predicate);
        int result = entityManager.createQuery(delete).executeUpdate();
        return result > 0;
    }

    @Override
    public List<Integer> fetchAllYears() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Double> query = builder.createQuery(Double.class);
        Root<Product> root = query.from(Product.class);

        Expression<Double> yearExp = builder.function(
                "date_part",
                Double.class,
                builder.literal("year"),
                root.get(Product_.createdOn)
        );

        query.select(yearExp)
                .distinct(true)
                .where(builder.isNotNull(root.get(Product_.createdOn)))
                .orderBy(builder.asc(yearExp));

        List<Double> yearsAsDouble = entityManager.createQuery(query).getResultList();

        return yearsAsDouble.stream()
                .map(Double::intValue)
                .collect(Collectors.toList());
    }
}
