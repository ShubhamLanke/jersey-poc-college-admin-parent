package com.dss.api.impl.service;

import com.dss.api.impl.dto.ProductDto;
import com.dss.api.impl.filter.ProductFilter;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
public interface ProductService {

    @GET
    @Path("/year")
    List<ProductDto> findByYear(@QueryParam("year") Integer year);

    @GET
    @Path("/years")
    public List<Integer> fetchAllYears();

    @POST
    @Path("/filter")
    List<ProductDto> filterProducts(ProductFilter productFilter);

    @POST
    Response createProduct(ProductDto productDTO);

    @PUT
    Response updateProduct(ProductDto productDTO);

    @POST()
    @Path("/save")
    Response saveAndUpdateProduct(ProductDto productDTO);

    @DELETE
    Boolean deleteProducts(List<Long> ids);
}
