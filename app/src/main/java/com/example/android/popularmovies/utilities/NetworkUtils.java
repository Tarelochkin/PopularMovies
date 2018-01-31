package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.JsonReader;
import android.util.Log;

import com.example.android.popularmovies.Movie;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.R.id.list;
import static android.os.Build.VERSION_CODES.M;
import static com.example.android.popularmovies.utilities.JSONUtils.getMovieAsPoster;

/**
 * Created by РЕПКА on 06.09.2017.
 */

public class NetworkUtils {

    private final static String LOG_TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String API_KEY = "0c680edd6fd5dad8c799a31684a18969";
    private static final String API_KEY_LABEL = "api_key";
    private static final String TRAILERS_PATH = "videos";
    private static final String REVIEWS_PATH = "reviews";

    private final static int MOVIE_DETAILS = 1024;
    private final static int POSTERS_LIST = 1023;
    private final static int TRAILERS_LIST = 1025;
    private final static int REVIEWS_LIST = 1026;

    private NetworkUtils() {
    }

    public static List<Movie> fetchMovieData(int movieId, String sortOrder) {
        List<Movie> moviesList = new ArrayList<>();
        URL jsonUrl;

        try {
            if (movieId == 0) {
                for (int i = 1; i <= 5; i++) {
                    Uri.Builder builder = buildUpon(sortOrder).appendQueryParameter("page", ""+i);
                    jsonUrl = new URL(builder.build().toString());
                    List<Movie> pageList = getInfoFromJsonUrl(jsonUrl, POSTERS_LIST);
                    moviesList.addAll(pageList);
                    builder.clearQuery();
                }
            } else {
                Uri.Builder builder = buildUpon("" + movieId);
                jsonUrl = new URL(builder.build().toString());
                moviesList = getInfoFromJsonUrl(jsonUrl, MOVIE_DETAILS);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(LOG_TAG, "Error closing InputStream or JsonReader");
        }
        return moviesList;
    }

    public static void updateMovieData(Movie movie) {
        int movieId = movie.getId();
        Uri.Builder trailersBuilder = buildUpon("" + movieId).appendPath(TRAILERS_PATH);
        Uri.Builder reviewsBuilder = buildUpon("" + movieId).appendPath(REVIEWS_PATH);

        try {
            URL trailersJsonUrl = new URL(trailersBuilder.build().toString());
            List<String[]> trailers = getInfoFromJsonUrl(trailersJsonUrl, TRAILERS_LIST);

            URL reviewsJsonUrl = new URL(reviewsBuilder.build().toString());
            List<String[]> reviews = getInfoFromJsonUrl(reviewsJsonUrl, REVIEWS_LIST);

            if (trailers.size() > 0) movie.setTrailers(trailers);

            if (reviews.size() > 0) {
                int reviewsLastIndex = reviews.size() - 1;
                int reviewPageCount = Integer.parseInt(reviews.get(reviewsLastIndex)[0]);
                reviews.remove(reviewsLastIndex);

                movie.setReviews(reviews);
                movie.setReviewPageCount(reviewPageCount);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(LOG_TAG, "Error closing InputStream or JsonReader");
        }
    }

    private static Uri.Builder buildUpon(String path) {
        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon()
                .appendPath(path)
                .appendQueryParameter(API_KEY_LABEL, API_KEY);

        return builder;
    }

    private static <T> List<T> getInfoFromJsonUrl(URL url, int contentType) throws IOException {
        T ob;
        List<T> dataList = new ArrayList<>();

        HttpsURLConnection connection = null;
        InputStream stream = null;
        JsonReader reader= null;

        try {
            connection = getConnection(url);
            stream = connection.getInputStream();
            reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
            reader.beginObject();

            if (contentType == MOVIE_DETAILS) {
                Movie movie = JSONUtils.getMovieDetails(reader);
                updateMovieData(movie);
                ob = (T) movie;
                dataList.add(ob);
            } else {
                while (!reader.nextName().equals("results")) reader.skipValue();
                reader.beginArray();
                while (reader.hasNext()) {
                    addIfValid(reader, dataList, contentType);
                }
                reader.endArray();
            }

            if (contentType == REVIEWS_LIST) {
                while (!reader.nextName().equals("total_pages")) reader.skipValue();
                String[] pageCountHolder = {reader.nextString()};
                ob = (T) pageCountHolder;
                dataList.add(ob);
            }
            while (reader.hasNext()) reader.skipValue();
            reader.endObject();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) reader.close();
            if (stream != null) stream.close();
            if (connection != null) connection.disconnect();
        }

        return dataList;
    }

    private static <T> void addIfValid(JsonReader reader, List<T> data, int contentType) throws IOException {
        T ob;

        try {
            switch (contentType) {
                case POSTERS_LIST:
                    ob = (T) JSONUtils.getMovieAsPoster(reader);
                    data.add(ob);
                    break;
                case TRAILERS_LIST:
                    ob = (T) JSONUtils.getTrailerData(reader);
                    data.add(ob);
                    break;
                case REVIEWS_LIST:
                    ob = (T) JSONUtils.getReviewsData(reader);
                    data.add(ob);
                    break;
                default:
                    while (reader.hasNext()) reader.skipValue();
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
            while (reader.hasNext()) reader.skipValue();
            reader.endObject();
        }
    }

    private static HttpsURLConnection getConnection(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();

        int responseCode = connection.getResponseCode();

        if (responseCode != HttpsURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + responseCode);
        }

        return connection;
    }

}
