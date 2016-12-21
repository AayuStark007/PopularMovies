package com.aayu.popMovi.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aayush on 03-07-2016.
 */
public class Review implements Parcelable {

    String rId;
    String author;
    String content;
    String url;

    public Review(String _rId, String _author, String _content, String _url){
        this.rId = _rId;
        this.author = _author;
        this.content = _content;
        this.url = _url;
    }

    protected Review(Parcel in) {
        this.rId = in.readString();
        this.author = in.readString();
        this.content = in.readString();
        this.url = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.rId);
        dest.writeString(this.author);
        dest.writeString(this.content);
        dest.writeString(this.url);
    }


    /**
     * Getter Methods
     */
    public String getrId(){
        return this.rId;
    }

    public String getAuthor(){
        return this.author;
    }

    public String getContent(){
        return this.content;
    }

    public String getUrl(){
        return this.getUrl();
    }


    /**
     * Setter Methods
     */
    public void setAuthor(String author){
        this.author = author;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setUrl(String url){
        this.url = url;
    }
}
