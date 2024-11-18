package com.demo.productMicroserves.productsService;

import com.demo.productMicroserves.rest.CreateProductRestModel;

public interface ProductService {

    public String createProduct(CreateProductRestModel product) throws Exception;
}
