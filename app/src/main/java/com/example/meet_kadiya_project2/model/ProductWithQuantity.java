package com.example.meet_kadiya_project2.model;

public class ProductWithQuantity {
    private ZaraProducts zaraProducts;
    private int quantity;
    private String size;
    public ProductWithQuantity() {
    }

    public ProductWithQuantity(ZaraProducts zaraProducts, int quantity, String size) {
        this.zaraProducts = zaraProducts;
        this.quantity = quantity;
        this.size = size;
    }

    public ZaraProducts getZaraProducts() {
        return zaraProducts;
    }

    public void setZaraProducts(ZaraProducts zaraProducts) {
        this.zaraProducts = zaraProducts;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
