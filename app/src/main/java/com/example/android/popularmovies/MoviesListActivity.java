package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class MoviesListActivity extends AppCompatActivity implements MovieAdapter.OnPosterClickListener {

    private final static String LOG_TAG = MoviesListActivity.class.getSimpleName();

    private final static String DETAILFRAGMENT_TAG = "DFTAG";
    private final static String MOVIE_ID_BUNDLE_KEY = "TMID_KEY";

    private boolean mTwoPane;
    private int mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState != null) {
                mMovieId = savedInstanceState.getInt(MOVIE_ID_BUNDLE_KEY);
            }

            DetailFragment fragment = createDetailFragment(mMovieId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();

        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }


//        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
//        SortingAdapter adapter = new SortingAdapter(this, getSupportFragmentManager());
//        viewPager.setAdapter(adapter);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);

    }

    private DetailFragment createDetailFragment(int movieId) {
        Bundle args = new Bundle();
        args.putInt(DetailFragment.MOVIE_ID, movieId);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMovieId > 0) {
            outState.putInt(MOVIE_ID_BUNDLE_KEY, mMovieId);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPosterClick(int movieId) {
        mMovieId = movieId;

        if (mTwoPane) {
            DetailFragment fragment = createDetailFragment(movieId);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, movieId);
            startActivity(intent);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.list_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.sort_popular:
//                if (sortOrder == SORT_BY_POPULAR) return false;
//                sortOrder = SORT_BY_POPULAR;
//                getSupportLoaderManager().restartLoader(0, null, this);
//                return true;
//            case R.id.sort_top_rated:
//                if (sortOrder == SORT_BY_TOP_RATED) return false;
//                sortOrder = SORT_BY_TOP_RATED;
//                getSupportLoaderManager().restartLoader(0, null, this);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }

}