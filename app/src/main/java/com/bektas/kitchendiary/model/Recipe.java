package com.bektas.kitchendiary.model;

import java.io.Serializable;

public class Recipe implements Serializable {
    private String id;
    private String title;
    private String preparationTime;
    private String cookingTime;
    private String imageUrl;

    public Recipe() {
    }

    public Recipe(String title, String preparationTime, String cookingTime, String imageUrl) {
        this.title = title;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(String preparationTime) {
        this.preparationTime = preparationTime;
    }

    public String getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(String cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
