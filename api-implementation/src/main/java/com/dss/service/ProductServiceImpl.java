package com.dss.service;

import com.dss.dto.CategoryDTO;
import com.dss.dto.ProductDTO;
import com.dss.entity.Category;
import com.dss.entity.Category_;
import com.dss.entity.Product;
import com.dss.entity.Product_;
import com.dss.filter.ProductFilter;
import com.dss.mapper.ProductMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {


    private final EntityManager entityManager;

    @Autowired
    public ProductServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<ProductDTO> findAll(String name) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        if (Strings.isNotEmpty(name)) {
            predicates.add(cb.like(root.get(Product_.name), "%" + name + "%"));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        List<Product> products = entityManager.createQuery(cq).getResultList();

        return products.stream()
                .map(ProductMapper::toDTO)
                .toList();
    }

    @Override
    public List<ProductDTO> findAllByPost(ProductFilter productFilter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder(); //TODO order by price
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        if (Strings.isNotEmpty(productFilter.getName())) {
            predicates.add(cb.like(root.get(Product_.name), "%" + productFilter.getName() + "%"));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        List<Product> products = entityManager.createQuery(cq).getResultList();

        return products.stream()
                .map(ProductMapper::toDTO)
                .toList();
    }

    @Override
    public List<ProductDTO> getAllProductsSortedByPrice(ProductFilter productFilter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();
        if (Strings.isNotEmpty(productFilter.getName())) {
            predicates.add(cb.like(root.get(Product_.name), "%" + productFilter.getName() + "%")); //TODO Change the impl for price
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(root.get(Product_.price)));

        List<Product> products = entityManager.createQuery(cq).getResultList();

        return products.stream()
                .map(ProductMapper::toDTO)
                .toList();
    }

    @Override
    public List<ProductDTO> filterProducts(ProductFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getStartDate() != null && filter.getEndDate() == null) {
            int year = filter.getStartDate().getYear();
            predicates.add(cb.equal(cb.function("YEAR", Integer.class, root.get("createdAt")), year));
        }

        if (filter.getEndDate() != null && filter.getStartDate() == null) {
            predicates.add(cb.lessThan(root.get("createdAt"), filter.getEndDate()));
        }

        if (filter.getStartDate() != null && filter.getEndDate() == null) {
            predicates.add(cb.greaterThan(root.get("createdAt"), filter.getStartDate()));
        }

        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            predicates.add(cb.between(root.get("createdAt"), filter.getStartDate(), filter.getEndDate()));
        }

        if (filter.getMinPrice() != null && filter.getMaxPrice() != null) {
            predicates.add(cb.between(root.get("price"), filter.getMinPrice(), filter.getMaxPrice()));
        }

        if (filter.getMinPrice() != null && filter.getStartDate() != null) {
            predicates.add(cb.or(
                    cb.lessThan(root.get("price"), filter.getMinPrice()),
                    cb.greaterThan(root.get("createdAt"), filter.getStartDate())
            ));
        }

        if (filter.getStartDate() != null && filter.getEndDate() != null && filter.getMinPrice() != null) {
            predicates.add(cb.and(
                    cb.between(root.get("createdAt"), filter.getStartDate(), filter.getEndDate()),
                    cb.greaterThan(root.get("price"), filter.getMinPrice())
            ));
        }

        if (filter.getEndDate() != null) {
            CriteriaQuery<Double> maxPriceQuery = cb.createQuery(Double.class);
            Root<Product> maxRoot = maxPriceQuery.from(Product.class);
            maxPriceQuery.select(cb.max(maxRoot.get("price")));
            maxPriceQuery.where(cb.lessThan(maxRoot.get("createdAt"), filter.getEndDate()));
            Double maxPrice = entityManager.createQuery(maxPriceQuery).getSingleResult();
            System.out.println("Max Price Before " + filter.getEndDate() + ": " + maxPrice);
        }

        predicates.add(cb.or(
                cb.isNull(root.get("price")),
                cb.isFalse(root.get("available"))
        ));

        if (filter.getExcludeCategories() != null && !filter.getExcludeCategories().isEmpty()) {
            predicates.add(cb.not(root.get("category").get("name").in(filter.getExcludeCategories())));
        }

        if (Boolean.TRUE.equals(filter.getCreatedOnWeekend())) {
            Expression<Integer> dayOfWeek = cb.function("DAYOFWEEK", Integer.class, root.get("createdAt"));
            predicates.add(dayOfWeek.in(1, 7)); // Sunday (1) or Saturday (7)
        }

        if (filter.getStartDate() != null) {
            int month = filter.getStartDate().getMonthValue();
            Expression<Integer> exprMonth = cb.function("MONTH", Integer.class, root.get("createdAt"));
            Expression<Integer> exprYear = cb.function("YEAR", Integer.class, root.get("createdAt"));
            predicates.add(cb.equal(exprMonth, month));
        }

        if (filter.getCategory() != null) {
            LocalDate today = LocalDate.now();
            LocalDate last25Days = today.minusDays(25);
            predicates.add(cb.and(
                    cb.greaterThanOrEqualTo(root.get("createdAt"), last25Days),
                    cb.equal(root.get("category").get("name"), filter.getCategory())
            ));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        List<Product> results = entityManager.createQuery(cq).getResultList();
        return results.stream().map(ProductMapper::toDTO).toList();
    }


    @Override
    public Response createProduct(ProductDTO productDTO) { //TODO uniquesnedd by prince and name
        if (productDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Product data is missing").build();
        }

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setAvailable(productDTO.getAvailable());
        Category category = findCategoryByName(productDTO.getCategoryDTO().getName());
        if (category == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Category not found").build();
        }
        product.setCategory(category);

        entityManager.persist(product);
        return Response.status(Response.Status.CREATED).build();
    }

    @Override
    public Response updateProduct(Long id, ProductDTO productDTO) {
        Product product = entityManager.find(Product.class, id);
        if (Objects.isNull(product)) {
            return Response.status(Response.Status.NOT_FOUND).entity("Product not found").build();
        }

        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setAvailable(productDTO.getAvailable());

        Category category = findCategoryByName(productDTO.getCategoryDTO().getName());
        if (Objects.isNull(category)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Category not found").build();
        }
        product.setCategory(category);

        entityManager.merge(product);
        return Response.ok().build();
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = entityManager.find(Product.class, id);
        if (product != null) {
            entityManager.remove(product);
        }
    }

    private Category findCategoryByName(String categoryName) {
        if (Strings.isEmpty(categoryName)) return null;

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Category> cq = cb.createQuery(Category.class);
        Root<Category> root = cq.from(Category.class);
        cq.where(cb.equal(root.get("name"), categoryName));

        List<Category> result = entityManager.createQuery(cq).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }
}
