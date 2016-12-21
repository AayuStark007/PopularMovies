package com.aayu.popMovi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.aayu.popMovi.adapters.ReviewAdapter;
import com.aayu.popMovi.adapters.TrailerAdapter;
import com.aayu.popMovi.interfaces.OnReviewTaskCompleted;
import com.aayu.popMovi.interfaces.OnTrailerTaskCompleted;
import com.aayu.popMovi.models.Movie;
import com.aayu.popMovi.models.Review;
import com.aayu.popMovi.models.Trailer;
import com.aayu.popMovi.tasks.FetchReviewTask;
import com.aayu.popMovi.tasks.FetchTrailerTask;
import com.aayu.popMovi.utils.Utility;
import com.linearlistview.LinearListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment implements OnReviewTaskCompleted, OnTrailerTaskCompleted {

    OnViewCreated mViewCreated;

    ReviewAdapter mReviewAdapter;
    TrailerAdapter mTrailerAdapter;

    LinearListView mlistView;
    LinearListView tlistView;

    CardView mReviewCard;
    CardView mTrailerCard;

    public boolean mFav = false;

    @Override
    public void onReviewLoaded(List<Review> reviews) {
        if (reviews.size() > 0) {
            mReviewCard.setVisibility(View.VISIBLE);
            if (mReviewAdapter != null) {
                mReviewAdapter.clear();
                for (Review review : reviews) {
                    mReviewAdapter.add(review);
                }
                mReviewAdapter.notifyDataSetChanged();
                //Log.d("ReviewAdapter", mReviewAdapter.getItem(0).getAuthor());

            }
        }

    }

    @Override
    public void onTrailerLoaded(List<Trailer> trailers) {
        if (trailers.size() > 0) {
            mTrailerCard.setVisibility(View.VISIBLE);
            if (mTrailerAdapter != null) {
                mTrailerAdapter.clear();
                for (Trailer trailer : trailers) {
                    mTrailerAdapter.add(trailer);
                }
                mTrailerAdapter.notifyDataSetChanged();
                //Log.d("ReviewAdapter", mReviewAdapter.getItem(0).getAuthor());

            }
        }
    }


    //Should be in utility
    public boolean isFav(){
        SharedPreferences pref = getActivity().getSharedPreferences("THEMOVIEFAVFILE", Context.MODE_PRIVATE);
        Log.d("Movieis", movie.getmId() + " " + pref.getString(new Integer(movie.getmId()).toString(), "no"));
        return pref.getString(new Integer(movie.getmId()).toString(), "no").equals("favourite");
    }

    public void setFav(String pref){
        SharedPreferences prefs = getActivity().getSharedPreferences("THEMOVIEFAVFILE", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(new Integer(movie.getmId()).toString(), pref);
        Log.d("Movie", movie.getmId() + " " + pref);
    }

    private void loadListView(View rootView) {
        mTrailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<Trailer>());

        tlistView = (LinearListView)rootView.findViewById(R.id.trailer_list);
        tlistView.setAdapter(mTrailerAdapter);


        mReviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<Review>());

        mlistView = (LinearListView)rootView.findViewById(R.id.review_list);
        mlistView.setAdapter(mReviewAdapter);
    }


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

    ToggleButton favStat;


    public MovieDetailsFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewCreated.setToolbarTitle(movie.getOriginal_title());
    }

    public void getReviews(){
        FetchReviewTask reviewTask = new FetchReviewTask(getActivity(), this);
        reviewTask.execute(movie.getmId());
    }

    public void getTrailers(){
        FetchTrailerTask trailerTask = new FetchTrailerTask(getActivity(), this);
        trailerTask.execute(movie.getmId());
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

        getTrailers();
        getReviews();

        loadListView(rootView);

        if(isFav()){
            Log.d("Favourite", "Set");
        }else{
            Log.d("Favourite", "Un_Set");
        }



        favStat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notifyFavPrefChanged(isChecked);
            }
        });

        return rootView;
    }

    public void notifyFavPrefChanged(boolean isChecked){
        if(isChecked){
            setFav("favourite");
        }else{
            setFav("not_favourite");
        }
    }

    private void setSynopsis() {
        synopsis.setText(movie.getOverview());
    }

    private void setRating() {
        rating.setText(movie.getVote_average());
    }

    private void setRelease() {
        String releaseData[] = movie.getRelease_date().split("-");
        String releaseDate =  months[(new Integer(releaseData[1]).intValue()) - 1] + ", " + releaseData[0];

        release.setText(releaseDate);
    }

    private void setTitle() {
        title.setText(movie.getOriginal_title());
    }

    private void loadPoster() {
        loadWithPicasso(movie.getPoster_path(), posView);
    }

    private void loadBackdrop() {
        int colH = screenHeight();

        if(isLandscape()) { //Landscape
            colH = (int)(colH * .60f);
        }else {
            colH = (int)(colH * .31f);
        }

        imgView.getLayoutParams().height = colH;

        loadWithPicasso(movie.getBackdrop_path(), imgView);
    }

    private void loadWithPicasso(String url, ImageView imgView) {
        Utility.picassoLoadImage(getContext(), imgView, url);  //Picasso.with(getContext()).load(url).into(imgView);
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

        mReviewCard = (CardView) rootView.findViewById(R.id.movie_reviews_card);
        mTrailerCard = (CardView) rootView.findViewById(R.id.movie_trailers_card);

        favStat = (ToggleButton) rootView.findViewById(R.id.setting_favourite);
    }
}
