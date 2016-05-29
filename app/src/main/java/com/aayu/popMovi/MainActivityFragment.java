package com.aayu.popMovi;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    MovieAdapter mMovieAdapter;
    GridView gridView;
    ArrayList<Movie> mMovieList = null;

    final static String PREF_POPULAR = "popular";
    final static String PREF_TOP = "top_rated";
    final static String KEY_MOVIES = "movies";
    final static String KEY_SORT = "sort_order";
    final static String KEY_DETAIL = "movie_object";

    String mSort = PREF_POPULAR;

    boolean saveExists = false;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if(mMovieList != null)
            outState.putParcelableArrayList(KEY_MOVIES, mMovieList);

        outState.putString(KEY_SORT, mSort);

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

    @Override
    public void onStart() {
        super.onStart();
        updateMovies(mSort);
    }

    public void updateMovies(String pref){
        FetchMovieTask movieTask = new FetchMovieTask();

        Log.d("SAVE??", saveExists + "");

        if(!saveExists)
            movieTask.execute(pref);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        gridView = (GridView)rootView.findViewById(R.id.movie_grid);
        int colW = (int)(getActivity().getResources().getDisplayMetrics().widthPixels * .31f);
        //Log.d("Grid Width", colW + "");
        gridView.setColumnWidth(colW);
        gridView.setAdapter(mMovieAdapter);

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

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie mData = mMovieAdapter.getItem(position);
                //Log.d("Movie data in main", mData.backdrop_path+"");

                Bundle movieData = new Bundle();
                movieData.putParcelable(KEY_DETAIL, mData);

                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtras(movieData);
                startActivity(intent);
            }
        });


        return rootView;
    }

    private class FetchMovieTask extends AsyncTask<String, Void, Movie[]>{

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
            Movie movies[] = null;

            final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "?";

            final String MOVIE_BASE_URL = "http://image.tmdb.org/t/p/";
            final String SIZE_POSTER = "w185";
            final String SIZE_BACKDROP = "w500";
            final String APIKEY_PARAM = "api_key";

            try {
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                //Log.d("Built URL", builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    movieJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    movieJsonStr = null;
                }
                movieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("MainActivityFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                movieJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivityFragment", "Error closing stream", e);
                    }
                }
            }

            if(movieJsonStr == null)
                return null;

            try {
                movies = getMovieData(movieJsonStr, MOVIE_BASE_URL, SIZE_POSTER, SIZE_BACKDROP);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSONException", e);
            }

            if(movies.length == 0){
                return null;
            }

            return movies;
        }

        private Movie[] getMovieData(String movieJsonStr, String baseUrl, String size_poster, String size_backdrop) throws JSONException{

            final String MDB_RESULTS = "results";
            final String MDP_POSTER = "poster_path";
            final String MDP_OVERVIEW = "overview";
            final String MDP_BACKDROP = "backdrop_path";
            final String MDP_RELEASE = "release_date";
            final String MDP_TITLE = "original_title";
            final String MDP_RATING = "vote_average";
            final String MDP_ID = "id";

            JSONObject movJson = new JSONObject(movieJsonStr);
            JSONArray resArray = movJson.getJSONArray(MDB_RESULTS);

            Movie data[] = new Movie[resArray.length()];

            //Log.d("movJson", "length : " + resArray.length());

            for(int i = 0; i < resArray.length(); i++){

                JSONObject posPath = resArray.getJSONObject(i);

                String poster_path = baseUrl + size_poster + posPath.getString(MDP_POSTER);
                String backdrop_path = baseUrl + size_backdrop + posPath.getString(MDP_BACKDROP);

                Movie mv = new Movie(posPath.getString(MDP_TITLE), poster_path,
                        posPath.getString(MDP_OVERVIEW), backdrop_path,
                        posPath.getString(MDP_RATING), posPath.getString(MDP_RELEASE), posPath.getInt(MDP_ID));

                data[i] = mv;

                //Log.d("Name", posPath.getString("original_title"));
                //Log.d("Poster Link", data[i]);
            }

            return data;

        }

        @Override
        protected void onPostExecute(Movie[] movies){
            super.onPostExecute(movies);

            if(movies == null) return;

            mMovieAdapter.clear();
            mMovieAdapter.addAll(movies);
            mMovieAdapter.notifyDataSetChanged();
        }
    }
}
