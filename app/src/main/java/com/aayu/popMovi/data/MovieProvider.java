package com.aayu.popMovi.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Aayush on 07-06-2016.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper movieDbHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_SORT_ORDER = 101;
    // FUTURE: Support for fetching movies with rating and movie_id

    private static final SQLiteQueryBuilder sMovieQueryBuilder;

    static{
        sMovieQueryBuilder = new SQLiteQueryBuilder();
    }

    //movie.sort_order = ?
    private static final String sSortOrderSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry.COLUMN_SORT_TYPE + " = ? ";



    // Attempt to build URI Matcher (which might fail -_- )
    static UriMatcher buildUriMatcher(){

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIE_WITH_SORT_ORDER);

        return uriMatcher;

    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Log.d("Query", uri.toString());

        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            case MOVIE:
            {
                Log.d("Matched", uri.toString() + " Matched!");
                //Log.d("Query Built", projection[0] + " " + selection + " " + " " + sortOrder );
                retCursor = movieDbHelper.getReadableDatabase().query(
                  MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                Log.d("Case:", "MOVIE" + " " + retCursor.getCount());
                break;
            }
            case MOVIE_WITH_SORT_ORDER:
            {
                retCursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        sSortOrderSelection,
                        new String[]{MovieContract.MovieEntry.getSortOrderFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            /**
             * Ok, so the movie grid view is not being populated.
             * The retCursor has no rows only column values which means that maybe the
             * SQL query is not proper or there is no data in the database.
             * Also the URI matcher is not matching the URIs properly.
             */

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch(match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_SORT_ORDER:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIE:{
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        Log.d("Query", "Return URI: " + returnUri.toString());
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numRowsAffected;

        if(null == selection) selection = "1";

        switch(match){
            case MOVIE: {
                numRowsAffected = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);

                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MovieContract.MovieEntry.TABLE_NAME + "'");

                break;
            }
            case MOVIE_WITH_SORT_ORDER: {
                numRowsAffected = db.delete(MovieContract.MovieEntry.TABLE_NAME, sSortOrderSelection, new String[]{MovieContract.MovieEntry.getSortOrderFromUri(uri)});

                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MovieContract.MovieEntry.TABLE_NAME + "'");

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Student: A null value deletes all rows.  In my implementation of this, I only notified
        // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
        // is null.
        // Oh, and you should notify the listeners here.
        if(numRowsAffected != 0) getContext().getContentResolver().notifyChange(uri, null);
        // Student: return the actual rows deleted
        return numRowsAffected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int numRowsAffected;

        switch(match){
            case MOVIE: {
                numRowsAffected = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(numRowsAffected != 0) getContext().getContentResolver().notifyChange(uri, null);

        return numRowsAffected;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                Log.d("Return Count", returnCount+"");
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
