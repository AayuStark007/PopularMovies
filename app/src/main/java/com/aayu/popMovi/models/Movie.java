package com.aayu.popMovi.models;

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
        this.original_title = in.readString();
        this.poster_path = in.readString();
        this.overview = in.readString();
        this.backdrop_path = in.readString();
        this.vote_average = in.readString();
        this.release_date = in.readString();
        this.id = in.readInt();
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
        dest.writeString(this.original_title);
        dest.writeString(this.poster_path);
        dest.writeString(this.overview);
        dest.writeString(this.backdrop_path);
        dest.writeString(this.vote_average);
        dest.writeString(this.release_date);
        dest.writeInt(this.id);
    }


    /**
     * Getter Methods
     */
    public String getOriginal_title(){
        return this.original_title;
    }

    public String getPoster_path(){
        return this.poster_path;
    }

    public String getOverview(){
        return this.overview;
    }

    public String getBackdrop_path(){
        return this.backdrop_path;
    }

    public String getVote_average(){
        return this.vote_average;
    }

    public String getRelease_date(){
        return this.release_date;
    }

    public int getmId(){
        return this.id;
    }


    /**
     * Setter Methods
     */
    public void setOriginal_title(String title){
        this.original_title = title;
    }

    public void setPoster_path(String path){
        this.poster_path = path;
    }

    public void setOverview(String overview){
        this.overview = overview;
    }

    public void setBackdrop_path(String path){
        this.backdrop_path = path;
    }

    public void setVote_average(String rate){
        this.vote_average = rate;
    }

    public void setRelease_date(String date){
        this.release_date = date;
    }

    public void setmId(int id){
        this.id = id;
    }

}
