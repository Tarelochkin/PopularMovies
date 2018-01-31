package com.example.android.popularmovies.utilities;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.example.android.popularmovies.Movie;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.author;
import static android.R.attr.id;
import static android.R.attr.key;

/**
 * Created by РЕПКА on 06.09.2017.
 */

public class JSONUtils {

    private final static String LOG_TAG = JSONUtils.class.getSimpleName();
    private JSONUtils() {}

    public static Movie getMovieAsPoster(JsonReader reader) throws IOException, IllegalStateException {
        int posterId = 0;
        String posterPath = "";

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                posterId = reader.nextInt();
            } else if (name.equals("poster_path") && reader.peek() == JsonToken.STRING) {
                posterPath = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        if (!posterPath.isEmpty()) {
            posterPath = posterPath.substring(1);
        }

        return new Movie(posterId, posterPath);
    }

    public static Movie getMovieDetails(JsonReader reader) throws IOException {
        int id = 0;
        double vote_average = 0;
        String title = "";
        String overview = "";
        String release_date = "";
        int runtime = 0;
        String poster_path = "";

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (reader.peek() != JsonToken.NULL) {
                if (name.equals("id")) {
                    id = reader.nextInt();
                } else if (name.equals("overview")) {
                    overview = reader.nextString();
                } else if (name.equals("poster_path")) {
                    poster_path = reader.nextString();
                } else if (name.equals("release_date")) {
                    release_date = reader.nextString();
                } else if (name.equals("runtime")) {
                    runtime = reader.nextInt();
                } else if (name.equals("title")) {
                    title = reader.nextString();
                } else if (name.equals("vote_average")) {
                    vote_average = reader.nextDouble();
                } else {
                    reader.skipValue();
                }
            } else {
                reader.skipValue();
            }
        }

        return new Movie(id, title, overview, vote_average, release_date, runtime, poster_path.substring(1));
    }

    public static String[] getTrailerData(JsonReader reader) throws IOException {
        String trailerYoutubeKey = null;
        String trailerName = "Trailer";

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("key") && reader.peek() == JsonToken.STRING) {
                trailerYoutubeKey = reader.nextString();
            } else if (name.equals("name") && reader.peek() == JsonToken.STRING) {
                trailerName = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        if (trailerYoutubeKey != null || !trailerName.equals("Trailer")) {
            return new String[]{trailerYoutubeKey, trailerName};
        } else {
            throw new IllegalStateException("No content for the trailer");
        }
    }

    public static String[] getReviewsData(JsonReader reader) throws IOException, IllegalStateException {
        String reviewAuthor = "Anonymous";
        String reviewText = "";

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("content")) {
                reviewText = reader.nextString();
            } else if (name.equals("author") && reader.peek() == JsonToken.STRING) {
                reviewAuthor = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new String[]{reviewAuthor, reviewText};
    }
}
