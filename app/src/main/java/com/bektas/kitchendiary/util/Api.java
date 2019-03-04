package com.bektas.kitchendiary.util;

import android.content.Context;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bektas.kitchendiary.BuildConfig;

public class Api {

    public static final String API_KEY = BuildConfig.ApiKey;
    public static final String API_ID = BuildConfig.ApiId;
    public static final String BASE_URL = "https://api.edamam.com/search";

    public static String getQueryUrl(String query, int startIndex, int endIndex){
        return String.format("%s?q=%s&app_id=%s&app_key=%s&from=%d&to=%d", BASE_URL, query, API_ID, API_KEY, startIndex, endIndex);
    }
}
