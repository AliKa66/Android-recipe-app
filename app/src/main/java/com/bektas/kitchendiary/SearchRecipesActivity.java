package com.bektas.kitchendiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bektas.kitchendiary.model.ApiRecipe;
import com.bektas.kitchendiary.util.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
//TODO fix-bug: startIndex is 0 when orientation is changed
public class SearchRecipesActivity extends AppCompatActivity {
    public static List<ApiRecipe> recipes = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchRecipesAdapter adapter;
    private RequestQueue queue;
    private ProgressBar pbDataLoad;
    private int startIndex = 0;
    private String query;
    private boolean canLoadMore;
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_recipes);
        queue = Volley.newRequestQueue(this);
        recyclerView = findViewById(R.id.recyclerView);
        pbDataLoad = findViewById(R.id.pbDataLoad);
        setupRecyclerView(recyclerView);

        // True if orientation changed
        if (savedInstanceState != null){
            Log.d("SearchRecipesActivity", "Adapter renewed");
        }

        Log.d("SearchRecipesActivity", "onCreate called");
        Log.d("SearchRecipesActivity", "Recipe size: " + recipes.size());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_recipes_activity_menu,menu );
        MenuItem searchItem = menu.findItem(R.id.search_recipes);
        final SearchView sv = (SearchView) searchItem.getActionView();
        sv.setQueryHint("Search for recipes...");
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()){
                    Log.d("SearchRecipesActivity", "Search item used in action bar." );
                    sv.clearFocus();
                    pbDataLoad.setVisibility(View.VISIBLE);
                    SearchRecipesActivity.recipes.clear();
                    loadData(query, startIndex, startIndex+15);
                    startIndex += 15;
                    return true;
                }else {
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        sv.setIconified(false);
        TextView tv = (TextView) sv.findViewById(androidx.appcompat.R.id.search_src_text);
        tv.setHintTextColor(Color.WHITE);
        sv.clearFocus();
    return true;
    }

    private void loadData(String query, int startIndex, int endIndex) {
        this.query = query;
        String url = Api.getQueryUrl(query, startIndex, endIndex);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    canLoadMore = json.getBoolean("more");
                    JSONArray jsonArray = json.getJSONArray("hits");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonRecipe = jsonArray.getJSONObject(i).getJSONObject("recipe");
                        Log.d("JsonRecipe",jsonRecipe.toString() );
                        JSONArray ingredients = jsonRecipe.getJSONArray("ingredientLines");
                        List<String> ingredientsList = new ArrayList<>();
                        for (int j = 0; j < ingredients.length(); j++) {
                            ingredientsList.add(ingredients.getString(j));
                        }
                        ApiRecipe recipe = new ApiRecipe(jsonRecipe.getString("url"),
                                jsonRecipe.getString("label"),
                                ingredientsList,
                                jsonRecipe.getInt("totalTime"),
                                jsonRecipe.getString("image"));
                        SearchRecipesActivity.recipes.add(recipe);
                    }
                    adapter.notifyDataSetChanged();
                    pbDataLoad.setVisibility(View.INVISIBLE);
                    isLoading = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                    pbDataLoad.setVisibility(View.INVISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pbDataLoad.setVisibility(View.INVISIBLE);
                Log.d("Volley Error", error.toString());
            }
        });
        queue.add(request);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        int numberOfColumns = 2;

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            numberOfColumns = 3;
        }
        final GridLayoutManager recipesLayoutManager =
                new GridLayoutManager(this, numberOfColumns, RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(recipesLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = recipesLayoutManager.getItemCount();
                int lastVisibleItem = recipesLayoutManager.findLastVisibleItemPosition();
                if (totalItemCount <= (lastVisibleItem + 2) && canLoadMore && !isLoading) {
                    isLoading = true;
                    Log.d("SearchRecipeActivity", "Renewing recyclerview. Scrolled to end.");
                    pbDataLoad.setVisibility(View.VISIBLE);
                    loadData(query, startIndex, startIndex+15);
                    startIndex += 15;
                }
            }
        });

        if (adapter == null){
            adapter = new SearchRecipesAdapter();
            recyclerView.setAdapter(adapter);
            Log.d("SearchRecipesActivity", "Created new adapter");
        }
    }
}
