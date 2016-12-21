package com.aayu.popMovi.interfaces;

import com.aayu.popMovi.models.Review;

import java.util.List;

/**
 * Created by Aayush on 03-07-2016.
 */
public interface OnReviewTaskCompleted {
    void onReviewLoaded(List<Review> reviews);
}
