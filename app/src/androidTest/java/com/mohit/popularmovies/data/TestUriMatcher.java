package com.mohit.popularmovies.data;

/**
 * Created by Mohit on 08-07-2016.
 */

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {
    private static final long TEST_MOVIE_ID = 1;
    private static final long TEST_MOVIE = 1;

    // content://com.example.android.sunshine.app/weather"
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_ID_ITEM = MovieContract.MovieEntry.buildMovieUri(1);
    private static final Uri TEST_TRAILER_DIR = MovieContract.TrailerEntry.CONTENT_URI;
    private static final Uri TEST_TRAILER_WITH_MOVIE_DIR = MovieContract.TrailerEntry.buildTrailerUriWithMovieId(TEST_MOVIE_ID);

    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The MOVIE URI was matched incorrectly.",
                MovieProvider.MOVIE, testMatcher.match(TEST_MOVIE_DIR));
        assertEquals("Error: The MOVIE WITH ID URI was matched incorrectly.",
                 MovieProvider.MOVIE_WITH_ID, testMatcher.match(TEST_MOVIE_WITH_ID_ITEM));
        assertEquals("Error: The TRAILER URI was matched incorrectly.",
                MovieProvider.TRAILER, testMatcher.match(TEST_TRAILER_DIR));
        assertEquals("Error: The TRAILER WITH MOVIE ID URI was matched incorrectly.",
                MovieProvider.TRAILER_WITH_MOVIE, testMatcher.match(TEST_TRAILER_WITH_MOVIE_DIR));
    }
}