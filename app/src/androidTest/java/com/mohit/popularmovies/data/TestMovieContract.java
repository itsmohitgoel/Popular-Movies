package com.mohit.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Mohit on 07-07-2016.
 */
public class TestMovieContract extends AndroidTestCase{

    public static final long TEST_MOVIE_ID = 1;
    public void testBuildTrailerUriWithMovieId() {
        Uri trailerUri = MovieContract.TrailerEntry.buildTrailerUriWithMovieId(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri  returned", trailerUri);
        assertEquals("Error: Uri doesn't match our expected result",
                "content://com.mohit.popularmovies.app/trailer/" + TEST_MOVIE_ID , trailerUri.toString());
    }
}
