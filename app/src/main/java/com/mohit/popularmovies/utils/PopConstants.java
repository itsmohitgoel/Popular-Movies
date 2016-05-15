package com.mohit.popularmovies.utils;

/**
 * Created by Mohit on 16-05-2016.
 */
public class PopConstants {
    // Our API key for themoviedb.org
    public static final String API_KEY_PARAM = "api_key";
    public static final String API_KEY = "ADD Your API KEY HERE";

    // API Query URLs with parameters
    public static final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
    public static final String SORT_PARAM = "sort_by";

    // JSON keys
    public static final String RESULTS = "results";
    public static final String ORIGINAL_TITLE = "original_title";
    public static final String POSTER_PATH = "poster_path";
    public static final String BACKDROP_PATH = "backdrop_path";
    public static final String OVERVIEW = "overview";
    public static final String RELEASE_DATE = "release_date";
    public static final String VOTE_AVERAGE = "vote_average";

    //IMAGE Query URls
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";
}
