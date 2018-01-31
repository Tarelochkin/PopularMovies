package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by РЕПКА on 28.10.2017.
 */

public class MovieLayoutManager extends GridLayoutManager {

    private int mExtraLayoutSpace;

    public MovieLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
        mExtraLayoutSpace = context.getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        return mExtraLayoutSpace;
    }

}
