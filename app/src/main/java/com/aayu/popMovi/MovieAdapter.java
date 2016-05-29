package com.aayu.popMovi;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by aayush on 27-05-2016.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private final List<Movie> items;

    public MovieAdapter(Context context, List<Movie> movie) {
        super(context, 0, movie);
        this.items = movie;
    }

    public List<Movie> getItems(){
        return items;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Movie movie = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_item, parent, false);
        }

        int colW = (int)(getContext().getResources().getDisplayMetrics().widthPixels * .31f);
        ((FrameLayout)convertView.findViewById(R.id.item_frame)).getLayoutParams().height = (int)(colW * 1.60f);

        ImageView movPos = (ImageView) convertView.findViewById(R.id.movie_image);
        //int colW = (int)(getContext().getResources().getDisplayMetrics().widthPixels * .31f);
        //int colH = (int)(getContext().getResources().getDisplayMetrics().heightPixels * .31f);
        //movPos.getLayoutParams().height = colH;
        //movPos.getLayoutParams().width = colW;
        //Log.d("MovieAdapter", "Loading image : " + movie);
        Picasso.with(getContext()).load(movie.poster_path).into(movPos);

        return convertView;
    }
}
