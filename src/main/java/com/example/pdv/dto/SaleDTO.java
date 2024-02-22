package com.example.pdv.dto;

import java.util.List;

public class SaleDTO {

    private Long userid;

    List<ProductSaleDTO> items;

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public List<ProductSaleDTO> getItems() {
        return items;
    }

    public void setItems(List<ProductSaleDTO> items) {
        this.items = items;
    }
}

