package com.example.android.popularmovies;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.example.android.popularmovies.utilities.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.example.android.popularmovies.utilities.NetworkUtils.fetchMovieData;

/**
 * Created by РЕПКА on 06.09.2017.
 */

public class MoviesLoader extends AsyncTaskLoader<List<Movie>> {
    private int mMovieId;
    private String mSortOrder;

    public MoviesLoader(Context context, int movieId, String sortOrder) {
        super(context);
        mMovieId = movieId;
        mSortOrder = sortOrder;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        return NetworkUtils.fetchMovieData(mMovieId, mSortOrder);
    }
}
