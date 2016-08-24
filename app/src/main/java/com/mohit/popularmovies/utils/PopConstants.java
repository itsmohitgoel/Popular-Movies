package com.mohit.popularmovies.utils;

/**
 * Created by Mohit on 16-05-2016.
 */
public class PopConstants {
    // Our API key for themoviedb.org
    public static final String API_KEY_PARAM = "api_key";
    public static final String API_KEY = "ADD Your API KEY HERE";

    // API Query URLs with parameters
    public static final String BASE_URL = "http://api.themoviedb.org/3/movie?";
    public static final String SORT_PARAM = "sort_by";
    public static final String TRAILER_PATH = "videos";

    // JSON keys for movie and trailers
    public static final String RESULTS = "results";
    public static final String MOVIE_API_ID = "id";
    public static final String ORIGINAL_TITLE = "original_title";
    public static final String POSTER_PATH = "poster_path";
    public static final String BACKDROP_PATH = "backdrop_path";
    public static final String OVERVIEW = "overview";
    public static final String RELEASE_DATE = "release_date";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String POPULARITY = "popularity";

    public static final String TRAILER_NAME = "name";
    public static final String TRAILER_SITE = "site";
    public static final String TRAILER_KEY = "key";
    public static final String TRAILER_SIZE = "size";
    public static final String TRAILER_TYPE = "type";

    //IMAGE Query URls
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";
    public static final String BASE_TRAILER_THUMB_URL = "http://img.youtube.com/vi/";
    public static final String YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch";
    public static final String YOUTUBE_VIDEO_PARAM = "v";

    //Movie intent key
    public static final String MOVIE_KEY = "movie";
    public static final String MOVIES_KEY = "movies";
}
