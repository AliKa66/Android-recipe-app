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
import com.bektas.kitchendiary.util.MyRecipes;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class MyRecipeAdapter
        extends RecyclerView.Adapter<MyRecipeAdapter.ViewHolder> {

    private final ChildEventListener mChildListener;

    private final MyRecipeListActivity mParentActivity;
    private final boolean mTwoPane;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Recipe recipe = (Recipe) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putInt(MyRecipeDetailFragment.RECIPE_INDEX, MyRecipes.indexOf(recipe));
                MyRecipeDetailFragment fragment = new MyRecipeDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, MyRecipeDetailActivity.class);
                intent.putExtra(MyRecipeDetailFragment.RECIPE_INDEX, MyRecipes.indexOf(recipe));

                context.startActivity(intent);
            }
        }
    };


    MyRecipeAdapter(MyRecipeListActivity parent,
                    boolean twoPane) {
        mParentActivity = parent;
        mTwoPane = twoPane;

        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                recipe.setId(dataSnapshot.getKey());
                MyRecipes.addRecipe(recipe);
                Log.d("Child added", dataSnapshot.getKey());
                notifyItemInserted(MyRecipes.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                int index = MyRecipes.indexOf(recipe);
                MyRecipes.updateRecipe(index, recipe);
                notifyItemChanged(index);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Recipe recipe = new Recipe();
                recipe.setId(dataSnapshot.getKey());
                int index = MyRecipes.indexOf(recipe);
                if (index != -1){
                    Log.d("Child removed", recipe.getId() + " index: " + index);
                    MyRecipes.deleteRecipe(index);
                    notifyItemRemoved(index);
                    notifyItemRangeChanged(index,MyRecipes.size());
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_recipe_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Recipe recipe = MyRecipes.getByIndex(position);
        holder.bind(recipe);

        holder.itemView.setTag(recipe);
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return MyRecipes.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvTotalTime;
        private ImageView imageRecipe;

        ViewHolder(View view) {
            super(view);
            tvTitle =  view.findViewById(R.id.tvTitle);
            tvTotalTime =  view.findViewById(R.id.tvTotalTime);
            imageRecipe = view.findViewById(R.id.imageRecipe);
        }

        public void bind(Recipe recipe) {
            int cookingTime = recipe.getCookingTime();
            int prepTime = recipe.getPreparationTime();
            int totalTime = cookingTime + prepTime;

            tvTitle.setText(recipe.getTitle());
            tvTotalTime.setText(String.format("Total time: %d min", totalTime));
            GlideUtil.showImage(recipe.getThumbUrl(), itemView.getContext(), imageRecipe);
        }
    }
}