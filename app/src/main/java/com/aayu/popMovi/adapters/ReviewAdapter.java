package com.aayu.popMovi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aayu.popMovi.R;
import com.aayu.popMovi.models.Review;

import java.util.List;

/**
 * Created by Aayush on 03-07-2016.
 */
public class ReviewAdapter extends ArrayAdapter<Review> {

    private final List<Review> reviews;

    public ReviewAdapter(Context context, List<Review> reviews){
        super(context, 0, reviews);
        this.reviews = reviews;
    }

    public List<Review> getItems(){
        return reviews;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Review rev = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_reviews_item, parent, false);
        }

        TextView author = (TextView) convertView.findViewById(R.id.review_author);
        author.setText(rev.getAuthor());

        TextView content = (TextView) convertView.findViewById(R.id.review_content);
        content.setText(rev.getContent());

        return convertView;
    }

}
