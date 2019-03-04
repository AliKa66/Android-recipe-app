package com.bektas.kitchendiary.util;

import com.bektas.kitchendiary.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class MyRecipes {
    private static List<Recipe> recipes = new ArrayList<>();

    public static List<Recipe> getRecipes() {
        return recipes;
    }

    public static void addRecipe(Recipe recipe){
        recipes.add(recipe);
    }

    public static void updateRecipe(int index, Recipe recipe){
        recipes.set(index,recipe);
    }

    public static void deleteRecipe(int index){
        recipes.remove(index);
    }

    public static int indexOf(Recipe recipe){
        return recipes.indexOf(recipe);
    }

    public static Recipe getByIndex(int index){
        return recipes.get(index);
    }

    public static int size(){
        return recipes.size();
    }

    public static void clear(){
        recipes.clear();
    }
}
