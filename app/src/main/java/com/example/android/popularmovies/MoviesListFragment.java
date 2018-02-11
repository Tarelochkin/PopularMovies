package com.example.android.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private View mEmptyView;
    private ProgressBar mLoading;
    private TextView mNoDataTextView;
    private Button mTryAgainButton;
    private int mLoaderId;

    public MoviesListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.list_view, container, false);

        mActivity = getActivity();

        mPostersView = (RecyclerView) rootView.findViewById(R.id.rv_movies_list);
        int spanCount = getResources().getInteger(R.integer.columns_count);
        MovieLayoutManager layoutManager = new MovieLayoutManager(mActivity, spanCount);
        mPostersView.setLayoutManager(layoutManager);
        mPostersView.setHasFixedSize(true);

        mAdapter = new MovieAdapter((MovieAdapter.OnPosterClickListener) mActivity);
        mPostersView.setAdapter(mAdapter);

        Bundle bundle = getArguments();
        mLoaderId = bundle.getInt(SortingAdapter.POSITION_KEY);

        mEmptyView = rootView.findViewById(R.id.master_empty_view);
        mLoading = (ProgressBar) mEmptyView.findViewById(R.id.loading_spinner);
        mNoDataTextView = (TextView) mEmptyView.findViewById(R.id.empty_view_text);
        mTryAgainButton = (Button) mEmptyView.findViewById(R.id.failed_connection_button);



        Log.d(LOG_TAG, "onCreateView LOADER ID = " + mLoaderId);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("MY_LOG", "ACTIVITY (RE)CREATED");

        if (mLoaderId == LOADER_ID_FAVORITES) {
            getLoaderManager().initLoader(
                    mLoaderId,
                    null,
                    new LocalLoaderCallbacks());
        } else {
            getLoaderManager().initLoader(
                    mLoaderId,
                    null,
                    new RemoteLoaderCallbacks());
        }

        super.onActivityCreated(savedInstanceState);
    }

    class RemoteLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Movie>> {
        @Override
        public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
            Log.d(LOG_TAG, "onCreateLoader LOADER ID = " + id);
            mNoDataTextView.setVisibility(View.GONE);
            mTryAgainButton.setVisibility(View.GONE);
            mLoading.setVisibility(View.VISIBLE);

            String sortPath = TOKEN_POPULAR;
            if (id == LOADER_ID_TOP_RATED) sortPath = TOKEN_TOP_RATED;

            return new MoviesLoader(mActivity, 0, sortPath);
        }

        @Override
        public void onLoadFinished(final Loader<List<Movie>> loader, List<Movie> data) {
            Log.d(LOG_TAG, "onLoadFinished LOADER ID = " + loader.getId());
            mLoading.setVisibility(View.GONE);

            if (data.size() > 0) {
                mEmptyView.setVisibility(View.GONE);
                mAdapter.setMoviesList(data);
            } else {
                Log.d(LOG_TAG, "REMOTE LOADER NO DATA");
                String emptyData = getResources().getString(R.string.api_data_unavailable);
                mEmptyView.setVisibility(View.VISIBLE);
                mNoDataTextView.setVisibility(View.VISIBLE);
                mNoDataTextView.setText(emptyData);
                mTryAgainButton.setVisibility(View.VISIBLE);
                mTryAgainButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        getLoaderManager().restartLoader(mLoaderId, null, new RemoteLoaderCallbacks());
                    }
                });
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
            List<Movie> movies = new ArrayList<>();

            if (cursor == null || cursor.getCount() < 1) {
                Log.d("MY_LOG", "cursor loader onLoadFinished with empty cursor");
                String emptyData = getResources().getString(R.string.favorites_empty);
                mEmptyView.setVisibility(View.VISIBLE);
                mNoDataTextView.setVisibility(View.VISIBLE);
                mNoDataTextView.setText(emptyData);
            } else {
                Log.d("MY_LOG", "cursor loader onLoadFinished with valid data");
                while (cursor.moveToNext()) {
                    int idIndex = cursor.getColumnIndex(MovieEntry.COLUMN_TMDID);
                    int imageIndex = cursor.getColumnIndex(MovieEntry.COLUMN_IMAGE);
                    Movie movie = new Movie(cursor.getInt(idIndex),
                            cursor.getString(imageIndex));
                    movies.add(movie);
                }
                mEmptyView.setVisibility(View.GONE);
                cursor.moveToPosition(-1);
            }
            mAdapter.setMoviesList(movies);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.setMoviesList(null);
            Log.d(LOG_TAG, "loader reset");
        }
    }
}
