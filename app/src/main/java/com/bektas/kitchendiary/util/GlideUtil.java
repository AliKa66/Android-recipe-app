package com.bektas.kitchendiary.util;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bektas.kitchendiary.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public final class GlideUtil{
    public static void showImage(String url, Context context, ImageView target){
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
            circularProgressDrawable.setStrokeWidth(5);
            circularProgressDrawable.setCenterRadius(30);
            circularProgressDrawable.start();
            Glide.with(context)
                    .load(url)
                    .placeholder(circularProgressDrawable)
                    .error(R.drawable.camera)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(target);
            if (url != null){
                Log.d("GlideUtil", "Loading image " + url);
            }
    }
}
