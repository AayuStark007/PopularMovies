package com.aayu.popMovi;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aayush on 27-05-2016.
 */
public class Movie implements Parcelable{

    String original_title;
    String poster_path;
    String overview;
    String backdrop_path;
    String vote_average;
    String release_date;
    int id;

    public Movie(String _original_title, String _poster_path,
                 String _overview, String _backdrop_path,
                 String _vote_average, String _release_date, int _id){

        this.original_title = _original_title;
        this.poster_path = _poster_path;
        this.overview = _overview;
        this.backdrop_path = _backdrop_path;
        this.vote_average = _vote_average;
        this.release_date = _release_date;
        this.id = _id;
    }

    protected Movie(Parcel in) {
        original_title = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        backdrop_path = in.readString();
        vote_average = in.readString();
        release_date = in.readString();
        id = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(original_title);
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeString(backdrop_path);
        dest.writeString(vote_average);
        dest.writeString(release_date);
        dest.writeInt(id);
    }
}
