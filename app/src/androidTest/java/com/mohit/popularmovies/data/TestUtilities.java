package com.mohit.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.mohit.popularmovies.data.MovieContract.MovieEntry;
import com.mohit.popularmovies.data.MovieContract.TrailerEntry;
import com.mohit.popularmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Provides methods and some test data to make it easier to test our database and
 * Content Provider.
 * Created by Mohit on 29-06-2016.
 */
public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Error: Empty Cursor returned in " + error, valueCursor.moveToFirst());
        validateCurrentRecord(valueCursor, expectedValues);
        assertFalse("Error: Resultset contains more than one record", valueCursor.moveToNext());
        valueCursor.close();
    }
    static void validateCurrentRecord(Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Error: Column " + columnName + " not found. ", idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Error: Value " + valueCursor.getString(idx) +
                    " did not match the expected Value " + expectedValue, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_MOVIE_ID, 368596);
        movieValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, "Back in the Day");
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, "A young boxer is taken under the wing of a mob" +
                " boss after his mother dies and his father is run out of town" +
                " for being an abusive alcoholic.");
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, "2016-05-20");
        movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, 3.49);
        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, "/is6QqgiPQlI3Wmk0bovqUFKM56B.jpg");
        movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, "/yySmUG29VgDdCROb9eer9L2kkKX.jpg");
        movieValues.put(MovieEntry.COLUMN_POPULARITY, 25.033);

        return movieValues;
    }

    static ContentValues createMovieWithMultipleTrailersValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_MOVIE_ID, 368596);
        movieValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, "Batman v Superman: Dawn of Justice");
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, "Fearing the actions of a god-like Super Hero left unchecked, " +
                "Gotham City’s own formidable, forceful vigilante takes on Metropolis’s most revered, " +
                "modern-day savior, while the world wrestles with what sort of hero it really needs. " +
                "And with Batman and Superman at war with one another, a new threat quickly arises," +
                " putting mankind in greater danger than it’s ever known before.");
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, "2016-03-23");
        movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, 5.62);
        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, "/cGOPbv9wA5gEejkUN892JrveARt.jpg");
        movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, "/vsjBeMPZtyB7yNsYY56XYxifaQZ.jpg");
        movieValues.put(MovieEntry.COLUMN_POPULARITY, 54.565);

        return movieValues;
    }

    static ContentValues createTrailerValues(long movieRowId) {
        ContentValues trailerValues = new ContentValues();
        trailerValues.put(TrailerEntry.COLUMN_MOVIE_ID, movieRowId);
        trailerValues.put(TrailerEntry.COLUMN_NAME, "Trailer 1");
        trailerValues.put(TrailerEntry.COLUMN_SITE, "YouTube");
        trailerValues.put(TrailerEntry.COLUMN_TRAILER_KEY, "FDz4WFjJ1zA");
        trailerValues.put(TrailerEntry.COLUMN_SIZE, 1080);
        trailerValues.put(TrailerEntry.COLUMN_TYPE, "Trailer");

        return trailerValues;
    }

    static ContentValues createTrailerValues(long movieRowId, String name, String site, String trailerKey, int size, String type) {
        ContentValues trailerValues = new ContentValues();
        trailerValues.put(TrailerEntry.COLUMN_MOVIE_ID, movieRowId);
        trailerValues.put(TrailerEntry.COLUMN_NAME, name);
        trailerValues.put(TrailerEntry.COLUMN_SITE, site);
        trailerValues.put(TrailerEntry.COLUMN_TRAILER_KEY, trailerKey);
        trailerValues.put(TrailerEntry.COLUMN_SIZE, size);
        trailerValues.put(TrailerEntry.COLUMN_TYPE, type);

        return trailerValues;
    }

    /*
        This will create multiple entries in trailers table for single movie,
        having differnet trailers
     */
    static ContentValues[] createMultipleTrailerValues(long movieRowId) {
        ContentValues trailer1 = createTrailerValues(movieRowId, "Exclusive Sneak", "YouTube", "6as8ahAr1Uc", 1080, "Teaser");
        ContentValues trailer2 = createTrailerValues(movieRowId, "Official Trailer 2", "YouTube", "fis-9Zqu2Ro", 1080, "Trailer");
        ContentValues trailer3 = createTrailerValues(movieRowId, "Official Final Trailer", "YouTube", "NhWg7AQLI_8", 1080, "Trailer");
        ContentValues trailer4 = createTrailerValues(movieRowId, "Official Teaser Trailer", "YouTube", "IwfUnkBfdZ4", 1080, "Trailer");
        ContentValues trailer5 = createTrailerValues(movieRowId, "TV Spot 1", "YouTube", "aUN0F5wKbGE", 1080, "Teaser");
        ContentValues trailer6 = createTrailerValues(movieRowId, "Official Comic-Con Trailer", "YouTube", "0WWzgGyAH6Y", 1080, "Trailer");

        ContentValues[] contentValuesArray = new ContentValues[]{trailer1, trailer2, trailer3, trailer4, trailer5, trailer6};
        return contentValuesArray;
    }

    static long insertBackInTheDayMovieValues(Context context) {
        //insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieValues();

        long movieRowId;
        movieRowId = db.insert(MovieEntry.TABLE_NAME, null, testValues);

        //verify we got a row back.
        assertTrue("Error: Failure to insert Back In the Day Movie values", movieRowId != -1);

        return  movieRowId;
    }

    static  class TestContentObserver extends ContentObserver{
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail(){
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }
}
