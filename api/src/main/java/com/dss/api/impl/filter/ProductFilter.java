package com.dss.api.impl.filter;

import com.dss.api.impl.dto.CategoryDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductFilter {

    private List<String> productName = new ArrayList<>();
    private List<CategoryDto> categoryDto = new ArrayList<>();
    private List<Integer> year = new ArrayList<>();
}
