package com.demo.core;


import java.math.BigDecimal;

public class ProductCreatedEvent {
    private String productId;
    private String tittle;
    private BigDecimal price;
    private Integer quantity;

    //used for deserialization purpose for cunsumer. deserialization process involves creating empty
    //instance of this class using no arg constructor and then populating the fields using gettters
    // setter method.
    public ProductCreatedEvent() {
    }

    public ProductCreatedEvent(String productId, String tittle, BigDecimal price, Integer quantity) {
        this.productId = productId;
        this.tittle = tittle;
        this.price = price;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

