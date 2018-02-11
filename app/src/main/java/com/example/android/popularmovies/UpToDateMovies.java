package com.example.android.popularmovies;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by РЕПКА on 01.02.2018.
 */

public class UpToDateMovies {
    private static List<Integer> list;

    static {
        list = new ArrayList<>();
    }

    static void add(int movieId) {
        if (!list.contains(movieId)) {
            list.add(movieId);
        }
    }

    static boolean contains(int movieId) {
        return list.contains(movieId);
    }
}
