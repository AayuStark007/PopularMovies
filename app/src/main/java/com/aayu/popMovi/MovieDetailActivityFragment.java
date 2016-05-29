package com.aayu.popMovi;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    OnViewCreated mViewCreated;

    public interface OnViewCreated{
        public void setToolbarTitle(String title);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mViewCreated = (OnViewCreated) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    final static String KEY_DETAIL = "movie_object";
    public Movie movie = null;

    final static String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    ImageView imgView;
    ImageView posView;

    TextView title;
    TextView release;
    TextView rating;
    TextView synopsis;


    public MovieDetailActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewCreated.setToolbarTitle(movie.original_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Bundle data = getActivity().getIntent().getExtras();
        if(data != null)
            movie = data.getParcelable(KEY_DETAIL);

        setupViews(rootView);

        loadBackdrop();
        loadPoster();

        setTitle();
        setRelease();
        setRating();
        setSynopsis();

        return rootView;
    }

    private void setSynopsis() {
        synopsis.setText(movie.overview);
    }

    private void setRating() {
        rating.setText(movie.vote_average);
    }

    private void setRelease() {
        String releaseData[] = movie.release_date.split("-");
        String releaseDate =  months[(new Integer(releaseData[1]).intValue()) - 1] + ", " + releaseData[0];

        release.setText(releaseDate);
    }

    private void setTitle() {
        title.setText(movie.original_title);
    }

    private void loadPoster() {
        loadWithPicasso(movie.poster_path, posView);
    }

    private void loadBackdrop() {
        int colH = screenHeight();

        if(isLandscape()) { //Landscape
            colH = (int)(colH * .60f);
        }else {
            colH = (int)(colH * .31f);
        }

        imgView.getLayoutParams().height = colH;

        loadWithPicasso(movie.backdrop_path, imgView);
    }

    private void loadWithPicasso(String url, ImageView imgView) {
        Picasso.with(getContext()).load(url).into(imgView);
    }

    private int screenHeight() {
        return getContext().getResources().getDisplayMetrics().heightPixels;
    }

    private int screenWidth() {
        return getContext().getResources().getDisplayMetrics().widthPixels;
    }

    private boolean isLandscape() {
        int screen_width = getContext().getResources().getDisplayMetrics().widthPixels;
        int screen_height = getContext().getResources().getDisplayMetrics().heightPixels;

        return (screen_width > screen_height);
    }

    private void setupViews(View rootView) {

        imgView = (ImageView)rootView.findViewById(R.id.backdrop_image);
        posView = (ImageView)rootView.findViewById(R.id.card_poster);

        title = (TextView)rootView.findViewById(R.id.card_title);
        release = (TextView) rootView.findViewById(R.id.release_date);
        rating = (TextView) rootView.findViewById(R.id.movie_rating);
        synopsis = (TextView) rootView.findViewById(R.id.plot_synopsis);
    }
}
