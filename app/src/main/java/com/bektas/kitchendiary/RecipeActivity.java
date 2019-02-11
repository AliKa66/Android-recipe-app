package com.bektas.kitchendiary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.bektas.kitchendiary.model.Recipe;
import com.bektas.kitchendiary.util.FirebaseUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RecipeActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText txtName;
    private EditText txtPreparationTime;
    private EditText txtCookingTime;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        FirebaseUtil.openFbReference("recipes");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        txtName = (EditText) findViewById(R.id.txtName);
        txtPreparationTime = (EditText) findViewById(R.id.txtPreparationTime);
        txtCookingTime = (EditText) findViewById(R.id.txtCookingTime);

        Intent intent = getIntent();
        Recipe recipe = (Recipe) intent.getSerializableExtra("Recipe");
        if (recipe == null){
            recipe = new Recipe();
        }
        this.recipe = recipe;

        txtName.setText(recipe.getTitle());
        txtPreparationTime.setText(recipe.getPreparationTime());
        txtCookingTime.setText(recipe.getCookingTime());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                saveRecipe();
                Toast.makeText(this,"Recipe saved", Toast.LENGTH_LONG).show();
                clear();
                backToList();
                return true;
            case R.id.delete_menu:
                deleteRecipe();
                Toast.makeText(this, "Recipe deleted", Toast.LENGTH_LONG).show();
                backToList();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveRecipe() {
        recipe.setTitle(txtName.getText().toString());
        recipe.setPreparationTime(txtPreparationTime.getText().toString());
        recipe.setCookingTime(txtCookingTime.getText().toString());
        if (recipe.getId() == null){
            mDatabaseReference.push().setValue(recipe);
        }else{
            mDatabaseReference.child(recipe.getId()).setValue(recipe);
        }
    }

    private void deleteRecipe(){
        if (recipe == null){
            Toast.makeText(this, "Please save the recipe before deleting", Toast.LENGTH_SHORT).show();
        }else {
            mDatabaseReference.child(recipe.getId()).removeValue();
        }
    }

    private void backToList(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void clear() {
        txtName.setText("");
        txtPreparationTime.setText("");
        txtCookingTime.setText("");
    }
}
