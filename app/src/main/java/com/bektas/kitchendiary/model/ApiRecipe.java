package com.bektas.kitchendiary.model;

import java.util.List;

public class ApiRecipe {
    private String webViewUrl;
    private String label;
    private List<String> ingredients;
    private int totalTime;
    private String imageUrl;

    public ApiRecipe(String webViewUrl, String label, List<String> ingredients, int totalTime, String imageUrl) {
        this.webViewUrl = webViewUrl;
        this.label = label;
        this.ingredients = ingredients;
        this.totalTime = totalTime;
        this.imageUrl = imageUrl;
    }

    public String getWebViewUrl() {
        return webViewUrl;
    }

    public void setWebViewUrl(String webViewUrl) {
        this.webViewUrl = webViewUrl;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
