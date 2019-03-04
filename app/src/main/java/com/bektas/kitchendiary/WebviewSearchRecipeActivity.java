package com.bektas.kitchendiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.bektas.kitchendiary.customtabs.CustomTabActivityHelper;

public class WebviewSearchRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_search_recipe);
        String weburl = getIntent().getStringExtra(SearchRecipesAdapter.RECIPE_WEB_URL);
        Log.d("WebviewSearchRecipeActivity", "Selected url: " + weburl);
        SharedPreferences prefs = getSharedPreferences("KitchenDiary", MODE_PRIVATE);
        String sharedUrl = prefs.getString(SearchRecipesAdapter.RECIPE_WEB_URL, null);

        if (sharedUrl != null){
            Log.d("WebviewSearchRecipeActivity", "Selected url sharedPref: " + sharedUrl);
        }


        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary, getTheme()));
        builder.addDefaultShareMenuItem();
        builder.setShowTitle(true);
        CustomTabsIntent customTabsIntent = builder.build();
        CustomTabActivityHelper.openCustomTab(this, customTabsIntent, Uri.parse(weburl),
                new CustomTabActivityHelper.CustomTabFallback() {
                    @Override
                    public void openUri(Activity activity, Uri uri) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        activity.startActivity(intent);
                    }
                });
        finish();
    }
}
