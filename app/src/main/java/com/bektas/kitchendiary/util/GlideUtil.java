package com.bektas.kitchendiary.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class GlideUtil {
    public static void showImage(String url, Context context, ImageView target){
        if (url != null && !url.isEmpty()){
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
            circularProgressDrawable.setStrokeWidth(5);
            circularProgressDrawable.setCenterRadius(30);
            circularProgressDrawable.start();
            Glide.with(context).load(url).placeholder(circularProgressDrawable).into(target);
            Log.d("GlideUtil", "Loading image " + url);
        }
    }
}
