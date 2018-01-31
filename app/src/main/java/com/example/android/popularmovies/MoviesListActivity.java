package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;


public class MoviesListActivity extends AppCompatActivity {

    private final static String LOG_TAG = MoviesListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);

        getSupportActionBar().setElevation(0);

//        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
//        SortingAdapter adapter = new SortingAdapter(this, getSupportFragmentManager());
//        viewPager.setAdapter(adapter);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);

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
