package com.dss.api.impl.service;

import com.dss.api.impl.dto.CategoryDto;
import com.dss.api.impl.filter.ProductFilter;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
public interface CategoryService {

    @GET
    List<CategoryDto> fetchAll();

    @POST
    @Path("/filter")
    List<CategoryDto> fetchAllCategory(ProductFilter filter);

    @POST
    @Path("/create")
    Response createCategory(CategoryDto categoryDto);

    @PUT
    Response updateCategory(CategoryDto categoryDto);

    @POST
    @Path("/save")
    Response saveAndUpdateCategory(CategoryDto categoryDto);

    @DELETE

    Boolean deleteCategory(List<Long> ids);
}
