package com.bektas.kitchendiary;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bektas.kitchendiary.model.Recipe;
import com.bektas.kitchendiary.util.FirebaseUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>{
    private List<Recipe> recipes;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;

    public RecipeAdapter(){
        FirebaseUtil.openFbReference("recipes");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        recipes = FirebaseUtil.recipes;
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                Log.d("Recipe: ",recipe.getTitle());
                recipe.setId(dataSnapshot.getKey());
                recipes.add(recipe);
                notifyItemInserted(recipes.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildListener);

    }
    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.rv_row, viewGroup, false);
        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int i) {
        Recipe recipe = recipes.get(i);
        recipeViewHolder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvPreparationTime;
        TextView tvCookingTime;
     public RecipeViewHolder(@NonNull View itemView) {
         super(itemView);
         tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
         tvPreparationTime = (TextView) itemView.findViewById(R.id.tvPreparationTime);
         tvCookingTime = (TextView) itemView.findViewById(R.id.tvCookingTime);
         itemView.setOnClickListener(this);
     }

     public void bind(Recipe recipe){
         tvTitle.setText(recipe.getTitle());
         tvPreparationTime.setText(recipe.getPreparationTime());
         tvCookingTime.setText(recipe.getCookingTime());
     }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d("Click", String.valueOf(position));
            Recipe selectedRecipe = recipes.get(position);
            Intent intent = new Intent(v.getContext(), RecipeActivity.class);
            intent.putExtra("Recipe", selectedRecipe);
            v.getContext().startActivity(intent);
     }
    }

}