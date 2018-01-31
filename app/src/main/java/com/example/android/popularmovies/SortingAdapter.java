package com.example.android.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import static android.R.attr.fragment;

/**
 * Created by РЕПКА on 18.11.2017.
 */

public class SortingAdapter extends FragmentPagerAdapter {

    private final static String LOG_TAG = FragmentPagerAdapter.class.getSimpleName();

    private Context mContext;
    static final String POSITION_KEY = "sorting_order";

    public SortingAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(LOG_TAG, "get item at position " + position);
        MoviesListFragment fragment = new MoviesListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION_KEY, position);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.tab_popular);
            case 1:
                return mContext.getString(R.string.tab_top);
            default:
                return mContext.getString(R.string.tab_favorite);
        }
    }
}
