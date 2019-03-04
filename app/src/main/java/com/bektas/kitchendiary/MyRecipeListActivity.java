package com.bektas.kitchendiary;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.bektas.kitchendiary.util.FirebaseUtil;
import com.bektas.kitchendiary.util.MyRecipes;
import com.google.firebase.auth.FirebaseAuth;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MyRecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
//TODO Fix bug: List is not correct when orientation is changed
public class MyRecipeListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipe_list);
        MyRecipes.clear();

        if (findViewById(R.id.recipe_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            FirebaseUtil.signIn(this);
        }else {
            recyclerView = findViewById(R.id.rvRecipes);
            assert recyclerView != null;
            setupRecyclerView((RecyclerView) recyclerView);
        }
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
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
                Intent intent = new Intent(this, AddOrEditMyRecipeActivity.class);
                startActivity(intent);
                return true;
            case R.id.search_recipes_menu:
                Intent searchIntent = new Intent(this, SearchRecipesActivity.class);
                startActivity(searchIntent);
                return true;
            case R.id.logout_menu:
                FirebaseUtil.logout(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (FirebaseUtil.mFireBaseAuth != null){
            FirebaseUtil.detachListener();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseUtil.mFireBaseAuth != null){
            FirebaseUtil.attachListener();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("ActivityResult", String.format("Requestcode: %s, resultcode: %s",requestCode,resultCode));
        if (requestCode == FirebaseUtil.RC_SIGN_IN && resultCode == RESULT_OK){
            View recyclerView = findViewById(R.id.rvRecipes);
            assert recyclerView != null;
            setupRecyclerView((RecyclerView) recyclerView);
        }
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.d("MyRecipeListActivity", "setupRecyclerVew called");
        FirebaseUtil.openFbReference("recipes", this);

        if (recyclerView.getAdapter() == null){
            MyRecipeAdapter adapter = new MyRecipeAdapter(this, mTwoPane);
            recyclerView.setAdapter(adapter);
        }

        if (FirebaseUtil.mFirebaseDatabase != null){
            LinearLayoutManager recipesLayoutManager =
                    new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            recyclerView.setLayoutManager(recipesLayoutManager);
        }
    }
}
