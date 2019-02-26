package com.bektas.kitchendiary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bektas.kitchendiary.model.Recipe;
import com.bektas.kitchendiary.util.FirebaseUtil;
import com.bektas.kitchendiary.util.GlideUtil;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final ChildEventListener mChildListener;

    private final RecipeListActivity mParentActivity;
    private final List<Recipe> mRecipes;
    private final boolean mTwoPane;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Recipe recipe = (Recipe) view.getTag();
            //TODO fix this
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putInt(RecipeDetailFragment.RECIPE_INDEX, mRecipes.indexOf(recipe));
                RecipeDetailFragment fragment = new RecipeDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailFragment.RECIPE_INDEX, mRecipes.indexOf(recipe));

                context.startActivity(intent);
            }
        }
    };


    RecipeAdapter(RecipeListActivity parent,
                  List<Recipe> recipes,
                  boolean twoPane) {
        mRecipes = recipes;
        mParentActivity = parent;
        mTwoPane = twoPane;

        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                recipe.setId(dataSnapshot.getKey());
                FirebaseUtil.addRecipe(recipe);
                Log.d("Child added", dataSnapshot.getKey());
                notifyItemInserted(mRecipes.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                int index = mRecipes.indexOf(recipe);
                FirebaseUtil.updateRecipe(index, recipe);
                notifyItemChanged(index);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Recipe recipe = new Recipe();
                recipe.setId(dataSnapshot.getKey());
                int index = mRecipes.indexOf(recipe);
                if (index != -1){
                    Log.d("Child removed", recipe.getId() + " index: " + index);
                    FirebaseUtil.deleteRecipe(index);
                    notifyItemRemoved(index);
                    notifyItemRangeChanged(index,mRecipes.size());
                }
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        FirebaseUtil.mDatabaseReference.addChildEventListener(mChildListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Recipe recipe = mRecipes.get(position);
        holder.bind(recipe);

        holder.itemView.setTag(recipe);
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvPreparationTime;
        private TextView tvCookingTime;
        private ImageView imageRecipe;
        private Context context;

        ViewHolder(View view) {
            super(view);
            context = view.getContext();
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvPreparationTime = (TextView) view.findViewById(R.id.tvPreparationTime);
            tvCookingTime = (TextView) view.findViewById(R.id.tvCookingTime);
            imageRecipe = view.findViewById(R.id.imageRecipe);
        }

        public void bind(Recipe recipe) {
            tvTitle.setText(recipe.getTitle());
            tvPreparationTime.setText(recipe.getPreparationTime());
            tvCookingTime.setText(recipe.getCookingTime());
            GlideUtil.showImage(recipe.getThumbUrl(), context, imageRecipe);
        }
    }
}