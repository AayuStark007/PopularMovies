package com.aayu.popMovi.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aayu.popMovi.R;
import com.aayu.popMovi.utils.Utility;
import com.aayu.popMovi.models.Trailer;

import java.util.List;

/**
 * Created by Aayush on 03-07-2016.
 */
public class TrailerAdapter extends ArrayAdapter<Trailer>{

    private final List<Trailer> trailers;

    public TrailerAdapter(Context context, List<Trailer> trailers){
        super(context, 0, trailers);
        this.trailers = trailers;
    }

    public List<Trailer> getItems(){
        return trailers;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        final Trailer tr = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_trailers_item, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.trailer_title);
        title.setText(tr.getName());

        ImageView imgView = (ImageView) convertView.findViewById(R.id.trailer_image);
        Utility.picassoLoadImage(getContext(), imgView, tr.getThumb_url());

        imgView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(tr.getVideo_url()));
                getContext().startActivity(intent);

            }
        });

        return convertView;
    }
}
