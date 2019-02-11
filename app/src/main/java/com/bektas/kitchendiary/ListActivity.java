package com.bektas.kitchendiary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bektas.kitchendiary.model.Recipe;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ListActivity extends AppCompatActivity {
    private List<Recipe> recipes;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecyclerView rvRecipes = (RecyclerView) findViewById(R.id.rvRecipes);
        final RecipeAdapter adapter = new RecipeAdapter();
        rvRecipes.setAdapter(adapter);
        LinearLayoutManager recipesLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvRecipes.setLayoutManager(recipesLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.insert_menu:
                Intent intent = new Intent(this, RecipeActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
