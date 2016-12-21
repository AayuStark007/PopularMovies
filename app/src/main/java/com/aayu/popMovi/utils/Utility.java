package com.aayu.popMovi.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by Aayush on 03-07-2016.
 */
public class Utility {

    public static void picassoLoadImage(final Context context, final ImageView imageView, final String url){
        Picasso.with(context)
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context)
                                .load(url)
                                .into(imageView);
                    }
                });
    }
}
