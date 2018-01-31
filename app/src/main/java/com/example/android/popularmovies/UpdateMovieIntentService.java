package com.example.android.popularmovies;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by РЕПКА on 25.01.2018.
 */

public class UpdateMovieIntentService extends IntentService {

    public UpdateMovieIntentService() {
        super("UpdateMovieIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int tmdbId = intent.getIntExtra(Intent.EXTRA_TEXT, 0);
        SyncMovieTask.updateMovie(tmdbId, this);
    }
}
