package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.minHeight;

/**
 * Created by РЕПКА on 08.09.2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final static String LOG_TAG = MovieAdapter.class.getSimpleName();
    private int viewHoldersCount;

//    private OnPosterClickListener mListener;
    private List<Movie> mMoviesList;
    private Context mContext;
    private static final float RATIO = 2.0f/3.0f;
    private boolean mFillFromDb;

    public MovieAdapter() {
        super();
        mMoviesList = new ArrayList<>();
//        mListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        viewHoldersCount++;
//        Log.d(LOG_TAG, "onCreateViewHolder " + viewHoldersCount);
        if (null == mContext) {
            mContext = parent.getContext();
        }
        int itemLayoutId = R.layout.movies_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(itemLayoutId, parent, false);
        int itemHeight = getItemHeight(parent.getWidth());
        return new MovieViewHolder(view, itemHeight);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieViewHolder holder, int position) {
        Movie currentMovie = mMoviesList.get(position);
        String posterPath = currentMovie.getPosterPath(mContext);
        Picasso
                .with(mContext)
                .load(posterPath)
                .error(R.drawable.placeholder_error)
                .fit()
                .into(holder.mPosterView);
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    public void setMoviesList(List<Movie> list) {
        mMoviesList = list;
        notifyDataSetChanged();
    }

    private int getItemHeight(int width) {
        int columnCount = mContext.getResources().getInteger(R.integer.columns_count);
        int itemWidth = width/columnCount;
        int itemHeight = (int) (itemWidth/RATIO);
        return itemHeight;
    }

    void setFilledFromDb(boolean filledFromDb) {
        mFillFromDb = filledFromDb;
    }

//    public interface OnPosterClickListener {
//        void onPosterClick(String movieId);
//    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mPosterView;

        public MovieViewHolder(View itemView, int height) {
            super(itemView);
            mPosterView = (ImageView) itemView.findViewById(R.id.list_movie_poster);
            mPosterView.getLayoutParams().height = height;
            mPosterView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedMovieId = mMoviesList.get(getAdapterPosition()).getId();
            Intent intent = new Intent(mContext, DetailsActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, clickedMovieId);
            if (mFillFromDb) intent.putExtra("FROM_DB", true);
            mContext.startActivity(intent);
        }
    }
}
