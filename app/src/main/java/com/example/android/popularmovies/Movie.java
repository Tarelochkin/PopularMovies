package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.popularmovies.utilities.NetworkUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by РЕПКА on 06.09.2017.
 */

public class Movie {
    private int mId;
    private String mTitle;
    private String mSynopsis;
    private double mRating;
    private String mReleaseDate;
    private int mRuntime;
    private String mPosterPath;
    private List<String[]> mTrailers = new ArrayList<>();
    private List<String[]> mReviews = new ArrayList<>();
    private int mReviewPageCount;

    private static final String BASE_POSTER_PATH = "http://image.tmdb.org/t/p";
    public static final String IMAGE_DIR = "images";
    private static final String SIZE_W92 = "w92";
    private static final String SIZE_W154 = "w154";
    private static final String SIZE_W185 = "w185";
    private static final String SIZE_W342 = "w342";
    private static final String SIZE_W500 = "w500";
    private static final String SIZE_W780 = "w780";
    private static final String SIZE_ORIGINAL = "original";


    public Movie(int id, String title, String synopsis, double rating, String date, int runtime, String posterPath) {
        mId = id;
        mTitle = title;
        mSynopsis = synopsis;
        mRating = rating;
        mReleaseDate = date;
        mRuntime = runtime;
        mPosterPath = posterPath;
    }

    public Movie(int id, String posterPath) {
        mId = id;
        mPosterPath = posterPath;
    }

    public void setTrailers(List<String[]> list) {
        mTrailers = list;
    }

    public void setReviews(List<String[]> list) {
        mReviews = list;
    }

    public void setReviewPageCount(int pageCount) {
        mReviewPageCount = pageCount;
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public double getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public int getRuntime() {
        return mRuntime;
    }

    public List<String[]> getTrailers() {
        return mTrailers;
    }

    public List<String[]> getReviews() {
        return mReviews;
    }

    public int getReviewPageCount() {
        return mReviewPageCount;
    }

    public String getRawPosterPath() {
        return mPosterPath;
    }

    public String getPosterPath(Context context) {
        File directory = context.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        File jpg = new File(directory, mPosterPath);
        if (jpg.exists()) {
            return "file://" + jpg.getPath();
        } else {
            Uri.Builder builder = Uri.parse(BASE_POSTER_PATH).buildUpon()
                    .appendPath(SIZE_W342)
                    .appendEncodedPath(mPosterPath);

            return builder.toString();
        }
    }

    public String toString() {
        return mId + ", " + mPosterPath;
    }

}
