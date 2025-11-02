package com.example.canteen.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "reviews")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer shopId;

    private String userName;

    private int rating;

    @Column(length = 2000)
    private String text;

    private Instant createdAt;

    public ReviewEntity() {}

    public ReviewEntity(Integer shopId, String userName, int rating, String text) {
        this.shopId = shopId;
        this.userName = userName;
        this.rating = rating;
        this.text = text;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getShopId() { return shopId; }
    public void setShopId(Integer shopId) { this.shopId = shopId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
