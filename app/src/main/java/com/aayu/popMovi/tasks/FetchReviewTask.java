package com.aayu.popMovi.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.aayu.popMovi.BuildConfig;
import com.aayu.popMovi.interfaces.OnReviewTaskCompleted;
import com.aayu.popMovi.models.Review;

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
import java.util.List;

/**
 * Created by Aayush on 03-07-2016.
 */
public class FetchReviewTask extends AsyncTask<Integer, Void, List<Review>> {

    private OnReviewTaskCompleted listener;

    private final String LOG_TAG = FetchReviewTask.class.getSimpleName();

    private final Context mContext;

    public FetchReviewTask(Context ctx, OnReviewTaskCompleted listnr) {
        mContext = ctx;
        listener = listnr;
    }

    @Override
    protected List<Review> doInBackground(Integer... params) {

        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String reviewJsonStr = null;

        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
        final String PARAM = "reviews";
        final String APIKEY_PARAM = "api_key";

        try {
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(params[0].toString())
                    .appendPath(PARAM)
                    .appendQueryParameter(APIKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            //Log.d(LOG_TAG, url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                reviewJsonStr = null;
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
                reviewJsonStr = null;
            }
            reviewJsonStr = buffer.toString();
            //Log.d(LOG_TAG, reviewJsonStr);

        }catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            e.printStackTrace();
            return null;

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

        try {
            return getReviewData(reviewJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private List<Review> getReviewData(String response) throws JSONException{
        final String M_RESULTS = "results";
        final String M_ID = "id";
        final String M_AUTHOR = "author";
        final String M_CONTENT = "content";
        final String M_URL = "url";

        JSONObject revJson = new JSONObject(response);

        JSONArray revArray = revJson.getJSONArray(M_RESULTS);

        List<Review> results = new ArrayList<>();

        for (int i = 0; i < revArray.length(); i++){
            JSONObject elem = revArray.getJSONObject(i);

            results.add(new Review(elem.getString(M_ID),
                    elem.getString(M_AUTHOR), elem.getString(M_CONTENT), elem.getString(M_URL)));
        }

        return results;
    }


    @Override
    protected void onPostExecute(List<Review> reviews) {
        super.onPostExecute(reviews);

        if (reviews != null) {

            listener.onReviewLoaded(reviews);

            /**
            if (reviews.size() > 0) {
                mReviewsCardview.setVisibility(View.VISIBLE);
                if (mReviewAdapter != null) {
                    mReviewAdapter.clear();
                    for (Review review : reviews) {
                        mReviewAdapter.add(review);
                    }
                }
            }*/
        }
    }
}
