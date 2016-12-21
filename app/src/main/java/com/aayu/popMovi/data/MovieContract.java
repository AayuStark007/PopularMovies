package com.aayu.popMovi.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by aayush on 07-06-2016.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.aayu.popMovi";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns{

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_TITLE = "original_title";
        public static final String COLUMN_POSTER = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_BACKDROP = "backdrop_path";
        public static final String COLUMN_RATING = "vote_average";
        public static final String COLUMN_RELEASE = "release_date";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_SORT_TYPE = "sort_order";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieUriWithSortOrder(String mOrder){
            return CONTENT_URI.buildUpon()
                    .appendPath(mOrder)
                    .build();
        }

        public static String getSortOrderFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

    }

}
