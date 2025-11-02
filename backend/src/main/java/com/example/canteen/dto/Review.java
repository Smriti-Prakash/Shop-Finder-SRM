package com.example.canteen.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Review {
    @NotNull
    private Integer shopId;

    @NotBlank
    private String user;

    @Min(1)
    @Max(5)
    private int rating;

    private String text;

    public Integer getShopId() { return shopId; }
    public void setShopId(Integer shopId) { this.shopId = shopId; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
