package com.example.android.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.data.PopularMoviesContract;
import com.example.android.popularmovies.data.PopularMoviesContract.MovieEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by РЕПКА on 18.11.2017.
 */

public class MoviesListFragment extends Fragment {

    private final static String LOG_TAG = MoviesListFragment.class.getSimpleName();

    private static final String TOKEN_POPULAR = "popular";
    private static final String TOKEN_TOP_RATED = "top_rated";
    private static final int LOADER_ID_POPULAR = 0;
    private static final int LOADER_ID_TOP_RATED = 1;
    private static final int LOADER_ID_FAVORITES = 2;
    private RecyclerView mPostersView;
    private MovieAdapter mAdapter;
    private FragmentActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.list_view, container, false);

        mActivity = getActivity();

        mPostersView = (RecyclerView) rootView.findViewById(R.id.rv_movies_list);
        int spanCount = getResources().getInteger(R.integer.columns_count);
        MovieLayoutManager layoutManager = new MovieLayoutManager(mActivity, spanCount);
        mPostersView.setLayoutManager(layoutManager);
        mPostersView.setHasFixedSize(true);

        mAdapter = new MovieAdapter();
        mPostersView.setAdapter(mAdapter);

        Bundle bundle = getArguments();
        int loaderId = bundle.getInt(SortingAdapter.POSITION_KEY);

        if (loaderId == LOADER_ID_FAVORITES) {
            mActivity.getSupportLoaderManager().initLoader(
                    loaderId,
                    null,
                    new LocalLoaderCallbacks());
        } else {
            mActivity.getSupportLoaderManager().initLoader(
                    loaderId,
                    null,
                    new RemoteLoaderCallbacks());
        }
        return rootView;
    }


    class RemoteLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Movie>> {
        @Override
        public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
            Log.d(LOG_TAG, "create list loader");
            String sortPath = TOKEN_POPULAR;
            if (id == LOADER_ID_TOP_RATED) sortPath = TOKEN_TOP_RATED;

            return new MoviesLoader(mActivity, 0, sortPath);
        }

        @Override
        public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
            Log.d(LOG_TAG, "posters count: " + data.size());
            if (data.size() > 0) {
                mAdapter.setFilledFromDb(false);
                mAdapter.setMoviesList(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Movie>> loader) {
            mAdapter.setMoviesList(null);
        }
    }

    class LocalLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.d(LOG_TAG, "create cursor loader");
            String[] projection = {MovieEntry.COLUMN_TMDID, MovieEntry.COLUMN_IMAGE};

            return new CursorLoader(
                    mActivity,
                    PopularMoviesContract.FULL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            Log.d(LOG_TAG, "cursor loader onLoadFinished");
            if (cursor == null) return;
            List<Movie> movies = new ArrayList<>();
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(MovieEntry.COLUMN_TMDID);
                int imageIndex = cursor.getColumnIndex(MovieEntry.COLUMN_IMAGE);
                Movie movie = new Movie(cursor.getInt(idIndex),
                        cursor.getString(imageIndex));
                movies.add(movie);
            }
            cursor.moveToPosition(-1);
            mAdapter.setFilledFromDb(true);
            mAdapter.setMoviesList(movies);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.setMoviesList(null);
            Log.d(LOG_TAG, "loader reset");
        }
    }
}
