package com.bektas.kitchendiary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bektas.kitchendiary.model.Recipe;
import com.bektas.kitchendiary.util.FirebaseUtil;
import com.bektas.kitchendiary.util.MyRecipes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * An activity representing a single Recipe detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeListActivity}.
 */
public class RecipeDetailActivity extends AppCompatActivity {
    private EditText txtName;
    private EditText txtPreparationTime;
    private EditText txtCookingTime;
    private EditText txtIngredients;
    private ImageView imageView;
    private Recipe currentRecipe;
    private MenuItem saveMenuItem;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            int recipeIndex = getIntent().getIntExtra(RecipeDetailFragment.RECIPE_INDEX,-1);
            currentRecipe = MyRecipes.getByIndex(recipeIndex);
            arguments.putInt(RecipeDetailFragment.RECIPE_INDEX,
                    recipeIndex);
            RecipeDetailFragment fragment = new RecipeDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_detail_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                navigateUpTo(new Intent(this, RecipeListActivity.class));
                return true;
            case R.id.edit_menu:
                Intent intent = new Intent(this, AddOrEditRecipeActivity.class);
                intent.putExtra("Recipe", currentRecipe);
                this.startActivity(intent);
                return true;
            case R.id.delete_menu:
                deleteRecipe();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteRecipe(){
        if (currentRecipe == null){
            Toast.makeText(this, "Please save the recipe before deleting", Toast.LENGTH_SHORT).show();
        }else {
            Log.d("Currentrecipe ID", currentRecipe.getId());
            FirebaseUtil.mDatabaseReference.child(currentRecipe.getId()).removeValue();
            Log.d("Recipe","Deleted");
            StorageReference ref;
            if (currentRecipe.getImageName() != null && !currentRecipe.getImageName().isEmpty()){
                ref = FirebaseUtil.mStorage.getReference().child(currentRecipe.getImageName());
                Log.d("Image", ref.getPath());
                ref.delete();
            }
            if (currentRecipe.getThumbName() != null && !currentRecipe.getThumbName().isEmpty()){
                ref = FirebaseUtil.mStorageRef.child("thumb").child(currentRecipe.getThumbName());
                Log.d("Thumb", ref.getPath());
                ref.delete().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Thumb", "NO SUCCESS");
                    }
                });
            }
            Toast.makeText(this, "Recipe deleted", Toast.LENGTH_LONG).show();
        }
    }
}
