package com.example.pdv.dto;

import java.util.List;

public class SaleInfoDTO {

    private String user;
    private String date;
    private List<ProductInfoDTO> products;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ProductInfoDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductInfoDTO> products) {
        this.products = products;
    }
}
