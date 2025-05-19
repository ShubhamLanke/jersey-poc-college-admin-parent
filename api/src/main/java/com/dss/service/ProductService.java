package com.dss.service;

import com.dss.dto.ProductDTO;
import com.dss.filter.ProductFilterDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;


@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ProductService {

    @GET
    List<ProductDTO> findAll(ProductFilterDTO productFilterDTO);

    @POST
    @Path("/fetch-all-products")
    List<ProductDTO> findAllByPost(ProductFilterDTO productFilterDTO);

    @GET
    List<ProductDTO> getAllProductsSortedByName(ProductFilterDTO filterDTO);

    @POST
    @Path("/sorted-by-price")
    List<ProductDTO> getAllProductsSortedByPrice(ProductFilterDTO filter);

    @POST
    @Path("/filter")
    List<ProductDTO> filterProducts(ProductFilterDTO filter);

    @POST
    Response createProduct(ProductDTO productDTO);

    @PUT
    @Path("/{id}")
    Response updateProduct(@PathParam("id") Long id, ProductDTO productDTO);

    @DELETE
    @Path("/{id}")
    Response deleteProduct(@PathParam("id") Long id);
}
