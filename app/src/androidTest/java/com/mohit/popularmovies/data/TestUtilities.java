package com.mohit.popularmovies.data;

import android.content.ContentValues;
import android.test.AndroidTestCase;

import com.mohit.popularmovies.data.MovieContract.MovieEntry;
import com.mohit.popularmovies.data.MovieContract.TrailerEntry;

/**
 * Provides methods and some test data to make it easier to test our database and
 * Content Provider.
 * Created by Mohit on 29-06-2016.
 */
public class TestUtilities extends AndroidTestCase {

    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, "Back in the Day");
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, "A young boxer is taken under the wing of a mob" +
                " boss after his mother dies and his father is run out of town" +
                " for being an abusive alcoholic.");
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, "2016-05-20");
        movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, 3.49);
        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, "/is6QqgiPQlI3Wmk0bovqUFKM56B.jpg");
        movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, "/yySmUG29VgDdCROb9eer9L2kkKX.jpg");

        return movieValues;
    }

    static ContentValues createTrailerValues(int movieRowId) {
        ContentValues trailerValues = new ContentValues();
        trailerValues.put(TrailerEntry.COLUMN_MOVIE_ID, movieRowId);
        trailerValues.put(TrailerEntry.COLUMN_NAME, "Trailer 1");
        trailerValues.put(TrailerEntry.COLUMN_SITE, "YouTube");
        trailerValues.put(TrailerEntry.COLUMN_TRAILER_KEY, "FDz4WFjJ1zA");
        trailerValues.put(TrailerEntry.COLUMN_SIZE, 1080);
        trailerValues.put(TrailerEntry.COLUMN_TYPE, "Trailer");

        return trailerValues;
    }

}
