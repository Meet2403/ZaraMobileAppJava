package com.example.meet_kadiya_project2.model;

import java.io.Serializable;
public class ZaraProducts implements Serializable{
    private String PImage;
    private String PName;
    private Double PPrice;
    private String Description;

    public ZaraProducts() {
        // Empty constructor
    }

    public ZaraProducts(String PImage, String PName, Double PPrice, String description) {
        this.PImage = PImage;
        this.PName = PName;
        this.PPrice = PPrice;
        this.Description = description;
    }

    public String getPImage() {
        return PImage;
    }

    public void setPImage(String PImage) {
        this.PImage = PImage;
    }

    public String getPName() {
        return PName;
    }

    public void setPName(String PName) {
        this.PName = PName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public double getPPrice() {
        return PPrice;
    }
    public void setPPrice(double PPrice) {
        this.PPrice = PPrice;
    }
}
