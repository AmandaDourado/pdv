package com.example.pdv.dto;

import java.util.List;

public class SaleDTO {

    private Long userid;

    List<ProductDTO> items;

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public List<ProductDTO> getItems() {
        return items;
    }

    public void setItems(List<ProductDTO> items) {
        this.items = items;
    }
}

