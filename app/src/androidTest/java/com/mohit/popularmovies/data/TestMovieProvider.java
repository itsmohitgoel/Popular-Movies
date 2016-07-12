package com.mohit.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.test.AndroidTestCase;

import com.mohit.popularmovies.data.MovieContract.MovieEntry;
import com.mohit.popularmovies.data.MovieContract.TrailerEntry;

/**
 * Created by Mohit on 11-07-2016.
 */
public class TestMovieProvider extends AndroidTestCase {
    public void deleteAllRecordsFromDB() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MovieEntry.TABLE_NAME, null, null);
        db.delete(TrailerEntry.TABLE_NAME, null, null);
        db.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromDB();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(), MovieProvider.class.getName());
        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            assertEquals("Error: Movie Provider is registered with authority: " + providerInfo.authority +
                            ", instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    MovieContract.CONTENT_AUTHORITY, providerInfo.authority);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: MovieProvider is not registered at " + mContext.getPackageName(), false);
        }
    }

    public void testGetType() {
        // content://com.mohit.popularmovies.app/movie/
        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE\n", MovieEntry.CONTENT_TYPE, type);

        // content://com.mohit.popularmovies.app/movie/2/
        long movieId = 2L;
        type = mContext.getContentResolver().getType(MovieEntry.buildMovieUri(movieId));
        assertEquals("Error: the MovieEntry CONTENT_URI with _ID should return MovieEntry.CONTENT_TYPE\n", MovieEntry.CONTENT_ITEM_TYPE, type);

        // content://com.mohit.popularmovies.app/trailer/
        type = mContext.getContentResolver().getType(TrailerEntry.CONTENT_URI);
        assertEquals("Error: the TrailerEntry.CONTENT_URI should return TrailerEntry.CONTENT_TYPE\n", TrailerEntry.CONTENT_TYPE, type);

        // content://com.mohit.popularmovies.app/trailer/1
        type = mContext.getContentResolver().getType(TrailerEntry.buildTrailerUriWithMovieId(movieId));
        assertEquals("Error: the TrailerEntry CONTENT_URI with movie _ID should return TrailerEntry.CONTENT_TYPE\n", TrailerEntry.CONTENT_TYPE, type);
    }

    public void testBasicMovieQuery() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();
        long movieRowId = TestUtilities.insertBackInTheDayMovieValues(mContext);

        Cursor movieCursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        assertTrue("Error: No records returned from the movie table", movieCursor.moveToFirst());
        TestUtilities.validateCurrentRecord(movieCursor, testValues);
        assertFalse("Error: More than one record returned from the movie table  ", movieCursor.moveToNext());

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movie Query didn't properly set Notification Uri", MovieEntry.CONTENT_URI, movieCursor.getNotificationUri());
        }
    }

    public void testBasicTrailerQuery() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testMovieValues = TestUtilities.createMovieValues();
        long movieRowId = TestUtilities.insertBackInTheDayMovieValues(mContext);

        ContentValues testTrailerValues = TestUtilities.createTrailerValues(movieRowId);

        long trailerRowId = db.insert(TrailerEntry.TABLE_NAME, null, testTrailerValues);
        assertTrue("Error: Unable to Insert TrailerEntry into trailer table", trailerRowId != -1);

        db.close();

        Cursor trailerCursor = mContext.getContentResolver().query(TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        TestUtilities.validateCursor("testBasicTrailerQuery", trailerCursor, testTrailerValues);
    }

    public void testMultipleTrailersQuery() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testMovieValues = TestUtilities.createMovieWithMultipleTrailersValues();
        long movieRowId = db.insert(MovieEntry.TABLE_NAME, null, testMovieValues);
        assertTrue("Error: Failure to insert movie, having multiple trailers associated," +
                " i.e Batman vs Superman", movieRowId != -1);

        ContentValues[] testTrailerValuesArray = TestUtilities.createMultipleTrailerValues(movieRowId);

        long trailerRowId;
        for (ContentValues testTrailerValues : testTrailerValuesArray) {
            trailerRowId = db.insert(TrailerEntry.TABLE_NAME, null, testTrailerValues);
            assertTrue("Error: Unable to Insert TrailerEntry into trailer table", trailerRowId != -1);
        }
        db.close();

        Cursor trailerCursor = mContext.getContentResolver().query(TrailerEntry.buildTrailerUriWithMovieId(movieRowId),
                null,
                null,
                null,
                null);

        assertTrue("Error : No trailer found in testMultipleTrailersQuery()", trailerCursor.moveToFirst());
        for (ContentValues testTrailerValues : testTrailerValuesArray) {
            TestUtilities.validateCurrentRecord(trailerCursor, testTrailerValues);
            trailerCursor.moveToNext();
        }
        trailerCursor.close();
    }

}
