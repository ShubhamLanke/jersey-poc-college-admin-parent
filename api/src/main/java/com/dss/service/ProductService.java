package com.dss.service;

import com.dss.dto.ProductDTO;
import com.dss.filter.ProductFilter;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.springframework.http.MediaType;

import java.util.List;


@Path("/products")
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.ALL_VALUE)
public interface ProductService {

    @GET
    List<ProductDTO> findAll(@QueryParam("name") String name);

    @POST
    @Path("/fetch-all-products")
    List<ProductDTO> findAllByPost(ProductFilter productFilter);

    @POST
    @Path("/sorted-by-price")
    List<ProductDTO> getAllProductsSortedByPrice(ProductFilter productFilter);

    @POST
    @Path("/filter")
    List<ProductDTO> filterProducts(ProductFilter productFilter);

    @POST
    Response createProduct(ProductDTO productDTO);

    @PUT
    @Path("/{id}")
    Response updateProduct(@PathParam("id") Long id, ProductDTO productDTO);

    @DELETE
    @Path("/{id}")
    void deleteProduct(@PathParam("id") Long id);
}
