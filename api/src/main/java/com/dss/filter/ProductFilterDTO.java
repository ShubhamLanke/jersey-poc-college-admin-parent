package com.dss.filter;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProductFilterDTO {
    private String name;
    private String category;
    private Double minPrice;
    private Double maxPrice;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean available;
    private List<String> excludeCategories;
    private Boolean createdOnWeekend;
}