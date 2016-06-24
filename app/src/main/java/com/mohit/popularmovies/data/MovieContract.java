package com.mohit.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Defines table and column names for the movies database
 * Created by Mohit on 25-06-2016.
 */
public class MovieContract {

    /* Inner class that defines the table contents  of the movie table*/
    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
    }
}
