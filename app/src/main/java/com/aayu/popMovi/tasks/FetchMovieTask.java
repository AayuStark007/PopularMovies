package com.aayu.popMovi.tasks;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.aayu.popMovi.BuildConfig;
import com.aayu.popMovi.data.MovieContract;
import com.aayu.popMovi.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Aayush on 08-06-2016.
 */
public class FetchMovieTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private final Context mContext;

    public FetchMovieTask(Context ctx){
        mContext = ctx;
    }

    long addMovie(String sortType, Movie movie){
        long locID;

        Cursor movCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_ID + " = ?",
                new String[]{movie.getmId() + ""},
                null
        );

        if(movCursor.moveToFirst()){
            int movieIdIndex = movCursor.getColumnIndex(MovieContract.MovieEntry._ID);
            locID = movCursor.getLong(movieIdIndex);
        }else{
            ContentValues movieVals = new ContentValues();

            movieVals.put(MovieContract.MovieEntry.COLUMN_ID, movie.getmId());
            movieVals.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            movieVals.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getOriginal_title());
            movieVals.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.getPoster_path());
            movieVals.put(MovieContract.MovieEntry.COLUMN_BACKDROP, movie.getBackdrop_path());
            movieVals.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getVote_average());
            movieVals.put(MovieContract.MovieEntry.COLUMN_RELEASE, movie.getRelease_date());
            movieVals.put(MovieContract.MovieEntry.COLUMN_SORT_TYPE, sortType);

            Uri insertedUri = mContext.getContentResolver().insert(
              MovieContract.MovieEntry.CONTENT_URI,
                    movieVals
            );

            locID = ContentUris.parseId(insertedUri);
        }
        movCursor.close();
        return locID;
    }

    @Override
    protected Void doInBackground(String... params) {

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

            // Create the request to MovieDBApi, and open the connection
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
            Log.d("JSON", movieJsonStr);
            getMovieData(movieJsonStr, MOVIE_BASE_URL, SIZE_POSTER, SIZE_BACKDROP, params[0]);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            e.printStackTrace();
            // If the code didn't successfully get the movie data, there's no point in attempting
            // to parse it.
            movieJsonStr = null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }

    private void getMovieData(String movieJsonStr, String baseUrl,
                                 String size_poster, String size_backdrop,
                                 String sortType) throws JSONException{

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

        Vector<ContentValues> cVVector = new Vector<ContentValues>(resArray.length());

        //Log.d("movJson", "length : " + resArray.length());

        for(int i = 0; i < resArray.length(); i++){

            JSONObject posPath = resArray.getJSONObject(i);

            String poster_path = baseUrl + size_poster + posPath.getString(MDP_POSTER);
            String backdrop_path = baseUrl + size_backdrop + posPath.getString(MDP_BACKDROP);

            ContentValues cv = new ContentValues();

            cv.put(MovieContract.MovieEntry.COLUMN_ID,  posPath.getInt(MDP_ID));
            cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, posPath.getString(MDP_OVERVIEW));
            cv.put(MovieContract.MovieEntry.COLUMN_TITLE, posPath.getString(MDP_TITLE));
            cv.put(MovieContract.MovieEntry.COLUMN_POSTER, poster_path);
            cv.put(MovieContract.MovieEntry.COLUMN_BACKDROP, backdrop_path);
            cv.put(MovieContract.MovieEntry.COLUMN_RATING, posPath.getString(MDP_RATING));
            cv.put(MovieContract.MovieEntry.COLUMN_RELEASE, posPath.getString(MDP_RELEASE));
            cv.put(MovieContract.MovieEntry.COLUMN_SORT_TYPE, sortType);

            //Log.d("Name", posPath.getString("original_title"));
            //Log.d("Poster Link", data[i]);

            cVVector.add(cv);
        }

        if(cVVector.size() > 0){
            Log.d("cVV size", cVVector.size()+"");
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);

            mContext.getContentResolver().delete(MovieContract.MovieEntry.buildMovieUriWithSortOrder(sortType), null, null);
            mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);

        }else{
            Log.d("Error", "cVV size 0");
        }

    }
}