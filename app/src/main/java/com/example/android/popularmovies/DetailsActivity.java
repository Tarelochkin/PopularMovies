package com.example.android.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.PopularMoviesContract;
import com.example.android.popularmovies.data.PopularMoviesContract.MovieEntry;
import com.example.android.popularmovies.databinding.ActivityDetailsBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    private final static String LOG_TAG = DetailsActivity.class.getSimpleName();

    private static final String YOUTUBE_PATH = "https://www.youtube.com/watch?v=";
    private static final int LOADER_ID_REMOTE = 3;
    private static final int LOADER_ID_LOCAL = 4;
    public static final String YOUTUBE_KEY_SEPARATOR = "_youtube_key_";
    public static final String TRAILER_NAME_SEPARATOR = "_trailer_name_";
    public static final String REVIEW_AUTHOR_SEPARATOR = "_review_author_";
    public static final String REVIEW_BODY_SEPARATOR = "_review_body_";

    String mSelection;
    String[] mSelectionArgs;
    private ActivityDetailsBinding mBinding;
    private Movie mMovie;
    private int mMovieId;
    private String mEmptyData;
    private List<String[]> trailersList;
    private String mPosterPath;
    private String mRawPosterPath;
    private boolean needsNotifying;
    private boolean isUpdated;
    private File mDirectory;

    @Override
    protected void onStop() {
        if (needsNotifying) {
            getContentResolver().notifyChange(PopularMoviesContract.FULL_CONTENT_URI, null);
        }
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        mEmptyData = getResources().getString(R.string.no_details);

        mMovieId = getIntent().getIntExtra(Intent.EXTRA_TEXT, 0);


        if (mMovieId != 0) {
            mDirectory = getDir(Movie.IMAGE_DIR, MODE_PRIVATE);

            mSelection = MovieEntry.COLUMN_TMDID + "=?";
            mSelectionArgs = new String[]{"" + mMovieId};
            if (movieIsInFavs()) {
                getSupportLoaderManager().initLoader(LOADER_ID_LOCAL, null, new LocalLoaderCallbacks());
            } else {
                getSupportLoaderManager().initLoader(LOADER_ID_REMOTE, null, new RemoteLoaderCallbacks());
            }


        } else {
            mBinding.detailsScreenTitle.setText(mEmptyData);
        }
    }

    private boolean movieIsInFavs() {
        String[] projection = new String[]{MovieEntry.COLUMN_TMDID};

        Cursor c = getContentResolver().query(
                PopularMoviesContract.FULL_CONTENT_URI,
                projection,
                mSelection,
                mSelectionArgs,
                null
        );

        if (c == null) return false;

        boolean found = c.getCount() > 0;
        c.close();

        return found;
    }

    private void populateScreen(Movie movie) {
        mMovieId = movie.getId();
        String title = movie.getTitle();
        String synopsis = movie.getSynopsis();
        double rating = movie.getRating();
        String rawReleaseDate = movie.getReleaseDate();
        String releaseDate = rawReleaseDate.split("-")[0];
        int rawRuntime = movie.getRuntime();

        mPosterPath = movie.getPosterPath(this);
        mRawPosterPath = movie.getRawPosterPath();

        mBinding.detailsScreenTitle.setText(title);
        mBinding.detailsScreenYear.setText(releaseDate);
        if (rawRuntime > 0) {
            String runtimeMins = rawRuntime + "min";
            mBinding.detailsScreenRuntime.setText(runtimeMins);
        }
        if (rating > 0) {
            String ratingFormatted = String.format("%.3s", rating) + "/10";
            mBinding.detailsScreenRating.setText(ratingFormatted);
        }
        mBinding.detailsScreenSynopsis.setText(synopsis);
        Picasso
                .with(this)
                .load(mPosterPath)
                .error(R.drawable.placeholder_error)
                .into(mBinding.detailsScreenPoster);;

        trailersList = movie.getTrailers();

        TrailersAdapter trailersAdapter = new TrailersAdapter(this, trailersList);
        mBinding.trailersListView.setAdapter(trailersAdapter);
        mBinding.trailersListView.setExpanded(true);

        mBinding.trailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String videoId = trailersList.get(position)[0];
                if (videoId == null) return;
                Uri trailerUri = Uri.parse(YOUTUBE_PATH + videoId);
                Intent i = new Intent(Intent.ACTION_VIEW, trailerUri);
                if (i.resolveActivity(getPackageManager()) != null) startActivity(i);
            }
        });

        List<String[]> reviewsList = movie.getReviews();

        if (reviewsList.size() > 0) {
            ReviewsAdapter reviewsAdapter = new ReviewsAdapter(this, reviewsList);

            for (int i = 0; i < reviewsList.size(); i++) {
                View item = reviewsAdapter.getView(i, null, null);
                mBinding.reviewsViewGroup.addView(item);
            }
        }
    }

    private void saveToFavorites(Movie movie) {

        long id = SyncMovieTask.syncMovie(movie, this, SyncMovieTask.SYNC_TO_ADD);

        if (id > -1) {
            makeAvailableForDeletion(mBinding.favoriteButton);
            savePosterToFile();
        } else {
            Toast.makeText(this, "Failed to add to Favorites", Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteMovie() {
        int deleted = getContentResolver().delete(
                PopularMoviesContract.FULL_CONTENT_URI,
                mSelection,
                mSelectionArgs);
        if (deleted > 0) {
            makeAvailableForFavorites(mBinding.favoriteButton);
            needsNotifying = true;
            deletePosterFromStorage();
        } else {
            Toast.makeText(this, "Unable to delete the movie", Toast.LENGTH_SHORT).show();
        }
    }

    private void makeAvailableForFavorites(TextView v) {
        v.setText(R.string.favorite_add);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFavorites(mMovie);
            }
        });
    }

    private void makeAvailableForDeletion(TextView v) {
        v.setText(R.string.favorite_delete);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMovie();
            }
        });
    }

    private void savePosterToFile() {
        mBinding.detailsScreenPoster.setDrawingCacheEnabled(true);
        Bitmap bitmap = mBinding.detailsScreenPoster.getDrawingCache();
        File jpg = new File(mDirectory, mRawPosterPath);

        try {
            if (!jpg.exists()) {
                FileOutputStream fos = new FileOutputStream(jpg);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                Log.d(LOG_TAG, "FILE SAVED");
            } else {
                Log.d(LOG_TAG, "FILE ALREADY EXISTS");
            }

        }
        catch (Exception e) {
            Log.e("SAVE_IMAGE", e.getMessage());
        }
    }

    private void deletePosterFromStorage() {
        File jpg = new File(mDirectory, mRawPosterPath);
        jpg.delete();
    }

    private void updateFavorite() {
        if (!isUpdated) {
            Intent i = new Intent(this, UpdateMovieIntentService.class);
            i.putExtra(Intent.EXTRA_TEXT, mMovieId);
            startService(i);

            isUpdated = true;
        }
    }

    class RemoteLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Movie>> {
        @Override
        public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
            return new MoviesLoader(DetailsActivity.this, mMovieId, null);
        }

        @Override
        public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
            if (data.size() > 0) {
                mMovie = data.get(0);
                populateScreen(mMovie);

                makeAvailableForFavorites(mBinding.favoriteButton);
            } else {
                mBinding.detailsScreenTitle.setText(mEmptyData);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Movie>> loader) {
        }
    }

    class LocalLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] projection = {MovieEntry.COLUMN_TMDID,
                    MovieEntry.COLUMN_NAME,
                    MovieEntry.COLUMN_IMAGE,
                    MovieEntry.COLUMN_YEAR,
                    MovieEntry.COLUMN_DURATION,
                    MovieEntry.COLUMN_RATING,
                    MovieEntry.COLUMN_SYNOPSIS,
                    MovieEntry.COLUMN_TRAILERS,
                    MovieEntry.COLUMN_REVIEWS
            };

            return new CursorLoader(
                    DetailsActivity.this,
                    PopularMoviesContract.FULL_CONTENT_URI,
                    projection,
                    mSelection,
                    mSelectionArgs,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor == null) return;
            cursor.moveToFirst();

            int idIndex = cursor.getColumnIndex(MovieEntry.COLUMN_TMDID);
            int nameIndex = cursor.getColumnIndex(MovieEntry.COLUMN_NAME);
            int synopsisIndex = cursor.getColumnIndex(MovieEntry.COLUMN_SYNOPSIS);
            int ratingIndex = cursor.getColumnIndex(MovieEntry.COLUMN_RATING);
            int yearIndex = cursor.getColumnIndex(MovieEntry.COLUMN_YEAR);
            int durationIndex = cursor.getColumnIndex(MovieEntry.COLUMN_DURATION);
            int imageIndex = cursor.getColumnIndex(MovieEntry.COLUMN_IMAGE);
            int trailersIndex = cursor.getColumnIndex(MovieEntry.COLUMN_TRAILERS);
            int reviewsIndex = cursor.getColumnIndex(MovieEntry.COLUMN_REVIEWS);

            mMovie = new Movie(cursor.getInt(idIndex),
                    cursor.getString(nameIndex),
                    cursor.getString(synopsisIndex),
                    cursor.getDouble(ratingIndex),
                    cursor.getString(yearIndex),
                    cursor.getInt(durationIndex),
                    cursor.getString(imageIndex)
            );

            List<String[]> trailers = new ArrayList<>();
            String trailersAsString = cursor.getString(trailersIndex);
            if (trailersAsString != null && !trailersAsString.isEmpty()) {
                String[] trailersAsArray = trailersAsString.split(TRAILER_NAME_SEPARATOR);
                for (String s : trailersAsArray) {
                    String[] trailerSplit = s.split(YOUTUBE_KEY_SEPARATOR);
                    trailers.add(trailerSplit);
                }
                mMovie.setTrailers(trailers);
            }

            List<String[]> reviews = new ArrayList<>();
            String reviewsAsString = cursor.getString(reviewsIndex);
            if (reviewsAsString != null && !reviewsAsString.isEmpty()) {
                String[] reviewsAsArray = reviewsAsString.split(REVIEW_BODY_SEPARATOR);
                for (String s : reviewsAsArray) {
                    String[] reviewsSplit = s.split(REVIEW_AUTHOR_SEPARATOR);
                    reviews.add(reviewsSplit);
                }
                mMovie.setReviews(reviews);
            }

            populateScreen(mMovie);
            makeAvailableForDeletion(mBinding.favoriteButton);

            updateFavorite();
            savePosterToFile();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }

}
