package com.aayu.popMovi;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.aayu.popMovi.adapters.MovieAdapter;
import com.aayu.popMovi.data.MovieContract;
import com.aayu.popMovi.models.Movie;
import com.aayu.popMovi.tasks.FetchMovieTask;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    MovieAdapter mMovieAdapter;
    GridView gridView;
    ArrayList<Movie> mMovieList = null;

    final static String PREF_POPULAR = "popular";
    final static String PREF_TOP = "top_rated";
    final static String KEY_MOVIES = "movies";
    final static String KEY_SORT = "sort_order";
    final static String KEY_DETAIL = "movie_object";

    String mSort = PREF_POPULAR;

    private static final int MOVIE_LOADER = 0;

    boolean saveExists = false;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_BACKDROP,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE,
            MovieContract.MovieEntry.COLUMN_ID,
    };

    static final int COL_TABLE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_POSTER = 2;
    static final int COL_MOVIE_OVERVIEW = 3;
    static final int COL_MOVIE_BACKDROP = 4;
    static final int COL_MOVIE_RATING = 5;
    static final int COL_MOVIE_RELEASE = 6;
    static final int COL_MOVIE_ID = 7;

    public MovieGridFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        //if(mMovieList != null)
        //    outState.putParcelableArrayList(KEY_MOVIES, mMovieList);

        //outState.putString(KEY_SORT, mSort);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mainfragment, menu);

        MenuItem menu_pref_popular = menu.findItem(R.id.pref_popular);
        MenuItem menu_pref_top = menu.findItem(R.id.pref_top_rated);

        if(mSort.equals(PREF_POPULAR)){
            if(!menu_pref_popular.isChecked())
                menu_pref_popular.setChecked(true);
        }else if(mSort.equals(PREF_TOP)){
            if(!menu_pref_top.isChecked())
                menu_pref_top.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.pref_popular:
                if(item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                if(mSort != PREF_POPULAR)
                    mSort = PREF_POPULAR;
                updateMovies(mSort);
                return true;
            case R.id.pref_top_rated:
                if(item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                if(mSort != PREF_TOP)
                    mSort = PREF_TOP;
                updateMovies(mSort);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void updateMovies(String pref){
        FetchMovieTask movieTask = new FetchMovieTask(getActivity());

        movieTask.execute(pref);
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        gridView = (GridView)rootView.findViewById(R.id.movie_grid);
        int colW = (int)(getActivity().getResources().getDisplayMetrics().widthPixels * .31f);
        //Log.d("Grid Width", colW + "");
        gridView.setColumnWidth(colW);
        gridView.setAdapter(mMovieAdapter);

        /**
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(KEY_SORT)){
                mSort = savedInstanceState.getString(KEY_SORT);
            }

            if(savedInstanceState.containsKey(KEY_MOVIES)){
                mMovieList = savedInstanceState.getParcelableArrayList(KEY_MOVIES);
                mMovieAdapter.clear();
                for(Movie mov : mMovieList){
                    mMovieAdapter.add(mov);
                }
                mMovieAdapter.notifyDataSetChanged();
                saveExists = true;
            }
        }
         **/

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Movie mData = null;
                //Log.d("Movie data in main", mData.backdrop_path+"");

                Cursor cursor = (Cursor)parent.getItemAtPosition(position);
                if(cursor != null) {

                    mData = getMovieObjFromCursor(cursor);

                    Bundle movieData = new Bundle();
                    movieData.putParcelable(KEY_DETAIL, mData);

                    Intent intent = new Intent(getActivity(), MovieDetails.class);
                    intent.putExtras(movieData);
                    startActivity(intent);
                }else{
                    //Log.e("Error", "null cursor");
                }
            }
        });


        return rootView;
    }

    public Movie getMovieObjFromCursor(Cursor cursor){
        // Return movie object from cursor.
        return new Movie(cursor.getString(COL_MOVIE_TITLE),
                cursor.getString(COL_MOVIE_POSTER),
                cursor.getString(COL_MOVIE_OVERVIEW),
                cursor.getString(COL_MOVIE_BACKDROP),
                cursor.getString(COL_MOVIE_RATING),
                cursor.getString(COL_MOVIE_RELEASE),
                cursor.getInt(COL_MOVIE_ID));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = MovieContract.MovieEntry._ID + " ASC";

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.buildMovieUriWithSortOrder(mSort),
                MOVIE_COLUMNS,
                MovieContract.MovieEntry.COLUMN_SORT_TYPE + " = ? ",
                new String[]{mSort},
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("MSG", "load finished");
        if(data == null)
            Log.d("Error", "null cursor");

        mMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }
}
