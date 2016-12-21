package com.aayu.popMovi.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aayu.popMovi.MovieGridFragment;
import com.aayu.popMovi.R;
import com.aayu.popMovi.utils.Utility;

/**
 * Created by aayush on 27-05-2016.
 */
public class MovieAdapter extends CursorAdapter {

    private static Context mContext;

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder)view.getTag();

        String poster_path = cursor.getString(MovieGridFragment.COL_MOVIE_POSTER);

        Utility.picassoLoadImage(mContext, viewHolder.posterView, poster_path);//Picasso.with(mContext).load(poster_path).into(viewHolder.posterView);
    }

    public static class ViewHolder{
        public final ImageView posterView;

        public ViewHolder(View view){
            int colW = (int)(mContext.getResources().getDisplayMetrics().widthPixels * .31f);
            ((FrameLayout)view.findViewById(R.id.item_frame)).getLayoutParams().height = (int)(colW * 1.60f);

            posterView = (ImageView) view.findViewById(R.id.movie_image);
        }
    }
}
