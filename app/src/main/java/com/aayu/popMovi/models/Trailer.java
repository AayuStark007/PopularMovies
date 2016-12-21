package com.aayu.popMovi.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aayush on 03-07-2016.
 */
public class Trailer implements Parcelable{

    String tId;
    String name;
    String video_url;
    String thumb_url;
    String type;

    public Trailer(String _tId, String _name, String _video_url, String _thumb_url, String _type){
        this.tId = _tId;
        this.name = _name;
        this.video_url = _video_url;
        this.thumb_url = _thumb_url;
        this.type = _type;
    }

    protected Trailer(Parcel in) {
        this.tId = in.readString();
        this.name = in.readString();
        this.video_url = in.readString();
        this.thumb_url = in.readString();
        this.type = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tId);
        dest.writeString(this.name);
        dest.writeString(this.video_url);
        dest.writeString(this.thumb_url);
        dest.writeString(this.type);
    }

    // id iso_639_1 iso_3166_1 key name site size type

    /**
     * Getter Methods
     */
    public String gettId(){
        return this.tId;
    }

    public String getName(){
        return this.name;
    }

    public String getVideo_url(){
        return this.video_url;
    }

    public String getThumb_url(){
        return this.thumb_url;
    }

    public String getType(){
        return this.type;
    }
}
