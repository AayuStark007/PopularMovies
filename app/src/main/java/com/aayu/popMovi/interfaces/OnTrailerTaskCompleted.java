package com.aayu.popMovi.interfaces;

import com.aayu.popMovi.models.Trailer;

import java.util.List;

/**
 * Created by Aayush on 03-07-2016.
 */
public interface OnTrailerTaskCompleted {
    void onTrailerLoaded(List<Trailer> trailers);
}
