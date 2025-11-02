package com.example.canteen.model;

import jakarta.persistence.*;

@Entity
@Table(name = "shops")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category;

    private Double lat;

    private Double lng;

    private String image;

    // store JSON text for menu and subshops (frontend expects arrays)
    @Column(columnDefinition = "TEXT")
    private String menu;

    @Column(columnDefinition = "TEXT")
    private String subshops;

    public Shop() {}

    public Shop(String name, String category, Double lat, Double lng, String image, String menu, String subshops) {
        this.name = name;
        this.category = category;
        this.lat = lat;
        this.lng = lng;
        this.image = image;
        this.menu = menu;
        this.subshops = subshops;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getSubshops() {
        return subshops;
    }

    public void setSubshops(String subshops) {
        this.subshops = subshops;
    }
}
