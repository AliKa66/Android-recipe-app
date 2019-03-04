package com.bektas.kitchendiary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bektas.kitchendiary.customtabs.CustomTabActivityHelper;
import com.bektas.kitchendiary.model.ApiRecipe;
import com.bektas.kitchendiary.util.GlideUtil;

import java.util.EventListener;
import java.util.EventListenerProxy;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.RecyclerView;

public class SearchRecipesAdapter extends RecyclerView.Adapter<SearchRecipesAdapter.ViewHolder>{

    public static final String RECIPE_WEB_URL = "index";

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ApiRecipe recipe = (ApiRecipe) v.getTag();
            Context context = v.getContext();
            Intent intent = new Intent(context, WebviewSearchRecipeActivity.class);
            intent.putExtra(SearchRecipesAdapter.RECIPE_WEB_URL, recipe.getWebViewUrl());
            context.startActivity(intent);
        }
    };
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_recipe_list, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApiRecipe recipe = SearchRecipesActivity.recipes.get(position);
        holder.bind(recipe);

        holder.itemView.setTag(recipe);
        holder.itemView.setOnClickListener(mOnClickListener);
    }


    @Override
    public int getItemCount() {
        return SearchRecipesActivity.recipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView recipeName;
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.txtSearchRecipe);
            image = itemView.findViewById(R.id.imgSearchRecipe);
        }


        public void bind(ApiRecipe recipe) {
            recipeName.setText(recipe.getLabel().toLowerCase());
            GlideUtil.showImage(recipe.getImageUrl(), itemView.getContext(), image);
        }
    }
}
