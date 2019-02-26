package com.bektas.kitchendiary.model;

import java.io.Serializable;
import java.util.Objects;

public class Recipe implements Serializable {
    private String id;
    private String title;
    private String preparationTime;
    private String cookingTime;
    private String ingredients;
    private String imageUrl;
    private String imageName;
    private String thumbUrl;
    private String thumbName;

    public Recipe() {
    }

    public Recipe(String title, String preparationTime, String cookingTime, String ingredients, String imageUrl, String imageName, String thumbUrl, String thumbName) {
        this.title = title;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.ingredients = ingredients;
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.thumbUrl = thumbUrl;
        this.thumbName = thumbName;
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getThumbName() {
        return thumbName;
    }

    public void setThumbName(String thumbName) {
        this.thumbName = thumbName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Recipe recipe = (Recipe) o;

        return id != null ? id.equals(recipe.id) : recipe.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
