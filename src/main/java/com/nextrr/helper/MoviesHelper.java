package com.nextrr.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import one.DefaultObjects;

public class MoviesHelper {

    public static Map<String, Object> getEditMovieMap(String name, int id, String date, String trailer, String cast,
            String movieType) {

        Map<String, Object> movieMap = new HashMap<String, Object>();
        movieMap.put("movieName", name);
        movieMap.put("movieId", id);
        movieMap.put("releaseDate", date);
        List<String> castList = Arrays.asList(cast.split("\\s*,\\s*"));
        movieMap.put("cast", castList);
        if (DefaultObjects.isNotEmpty(trailer)) {
            movieMap.put("trailer", trailer);
        }
        movieMap.put("movieType", movieType);

        return movieMap;
    }

    public static Map<String, Object> getMovieMap(String name, int id, String date, List<String> trailer, String cast) {

        Map<String, Object> movieMap = new HashMap<String, Object>();
        movieMap.put("movieName", name);
        movieMap.put("movieId", id);
        movieMap.put("releaseDate", date);
        movieMap.put("cast", cast);
        if (DefaultObjects.isNotEmpty(trailer)) {
            movieMap.put("trailer", trailer);
        }

        return movieMap;
    }
}