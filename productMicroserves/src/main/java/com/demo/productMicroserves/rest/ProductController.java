package com.demo.productMicroserves.rest;

import com.demo.productMicroserves.productsService.ProductService;
import com.demo.productMicroserves.productsService.ProductServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final Logger LOGGER= LoggerFactory.getLogger(ProductController.class);

    ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/")
    ResponseEntity<Object> createProduct(@RequestBody CreateProductRestModel product){
        String productId = null;
        try {
            productId = productService.createProduct(product);
        } catch (Exception e) {

            LOGGER.error(e.getMessage(),e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(new Date(),e.getMessage()
            ,"/products"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }
}