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
import com.google.firebase.auth.FirebaseAuth;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

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
            View recyclerView = findViewById(R.id.rvRecipes);
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
                Intent intent = new Intent(this, AddOrEditRecipeActivity.class);
                startActivity(intent);
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
        FirebaseUtil.openFbReference("recipes", this);
        if (FirebaseUtil.mFirebaseDatabase != null){
            recyclerView.setAdapter(new RecipeAdapter(this, mTwoPane));
            LinearLayoutManager recipesLayoutManager =
                    new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            recyclerView.setLayoutManager(recipesLayoutManager);
        }
    }
}
