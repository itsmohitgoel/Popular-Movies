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

    /* Inner class that defines the table contents of the trailer table */
    public static final class TrailerEntry implements BaseColumns{
        public static final String TABLE_NAME = "trailer";

        //Column with the foreign key into 'movie' table
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_TRAILER_KEY = "key";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_TYPE = "type";

    }
}
