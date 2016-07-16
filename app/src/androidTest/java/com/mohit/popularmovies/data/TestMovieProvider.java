package com.mohit.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                TrailerEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(MovieEntry.CONTENT_URI,
                null,
                null);

        Cursor movieCursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        assertEquals("Error: Records not deleted from MOVIE table during delete", 0, movieCursor.getCount());
        movieCursor.close();

        Cursor trailerCursor = mContext.getContentResolver().query(TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        assertEquals("Error: Records not deleted from trailer table during delete", 0, trailerCursor.getCount());
        trailerCursor.close();

    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
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

    public void testInsert() {
        ContentValues testMovieValues = TestUtilities.createMovieValues();

        TestUtilities.TestContentObserver tco = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testMovieValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieRowId = ContentUris.parseId(movieUri);

        assertTrue("Error: movie insertion failed in movie table", movieRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        TestUtilities.validateCursor(" testInsert(). Error validating MovieEntry insert.", cursor, testMovieValues);


        ContentValues testTrailerValues = TestUtilities.createTrailerValues(movieRowId);

        tco = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TrailerEntry.CONTENT_URI, true, tco);
        Uri trailerInsertUri = mContext.getContentResolver().insert(TrailerEntry.CONTENT_URI, testTrailerValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long trailerRowId = ContentUris.parseId(trailerInsertUri);

        assertTrue("Error: trailer insertion failed in trailer table", trailerRowId != -1);

        Cursor trailerCursor = mContext.getContentResolver().query(TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        TestUtilities.validateCursor(" testInsert(). Error validating TrailerEntry insert.", trailerCursor, testTrailerValues);

        testMovieValues.putAll(testTrailerValues);
        // Get the joined Movie and Trailer data
        Cursor trailerMovieCursor = mContext.getContentResolver().query(
                TrailerEntry.buildTrailerUriWithMovieId(movieRowId),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testInsert. Error validating the joined trailer and movie data",
                trailerMovieCursor, testMovieValues);
    }

    public void testDelete() {
        testInsert();

        // Register a content observer for our movie table
        TestUtilities.TestContentObserver movieObserver = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);

        // Register a content observer for our trailer table
        TestUtilities.TestContentObserver trailerObserver = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TrailerEntry.CONTENT_URI, true, trailerObserver);

        deleteAllRecordsFromProvider();

        movieObserver.waitForNotificationOrFail();
        trailerObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
        mContext.getContentResolver().unregisterContentObserver(trailerObserver);
    }

    public void testUpdate() {
        // First Test updation of Movie Table entries
        ContentValues movieValues = TestUtilities.createMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, movieValues);

        long movieRowId = ContentUris.parseId(movieUri);

        assertTrue("Error: zero record inserted in movie table", movieRowId != -1);

        ContentValues movieValuesUpdated = new ContentValues(movieValues);
        movieValuesUpdated.put(MovieEntry._ID, movieRowId);
        movieValuesUpdated.put(MovieEntry.COLUMN_ORIGINAL_TITLE, "Updated Title of Back In the Day");

        Cursor movieCursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        TestUtilities.TestContentObserver movieObserver = TestUtilities.TestContentObserver.getTestContentObserver();
        movieCursor.registerContentObserver(movieObserver);

        int count = mContext.getContentResolver().update(MovieEntry.CONTENT_URI,
                movieValuesUpdated,
                MovieEntry._ID + " = ?",
                new String[]{Long.toString(movieRowId)});

        movieObserver.waitForNotificationOrFail();

        movieCursor.unregisterContentObserver(movieObserver);
        movieCursor.close();

        Cursor updateCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                MovieEntry.COLUMN_ORIGINAL_TITLE + " = ?",
                new String[]{"Updated Title of Back In the Day"},
                null
        );

        TestUtilities.validateCursor("testUpdate. Error validating Movie entry table",
                updateCursor, movieValuesUpdated);


        // Now test updation of entries in trailer table
        ContentValues trailerValues = TestUtilities.createTrailerValues(movieRowId);
        Uri trailerUri = mContext.getContentResolver().insert(TrailerEntry.CONTENT_URI, trailerValues);

        long trailerRowId = ContentUris.parseId(trailerUri);
        assertTrue("Error: zero row inserted in trailer table", trailerRowId != -1);

        ContentValues trailerValuesUpdated = new ContentValues(trailerValues);
        trailerValuesUpdated.put(TrailerEntry.COLUMN_MOVIE_ID, 1378);

        Cursor trailerCursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.TestContentObserver trailerObserver = TestUtilities.TestContentObserver.getTestContentObserver();
        trailerCursor.registerContentObserver(trailerObserver);

        int trailerCount = mContext.getContentResolver().update(
                TrailerEntry.CONTENT_URI,
                trailerValuesUpdated,
                TrailerEntry._ID + " = ? ",
                new String[]{Long.toString(trailerRowId)}
        );

        trailerObserver.waitForNotificationOrFail();

        trailerCursor.unregisterContentObserver(trailerObserver);
        trailerCursor.close();

        Cursor trailerCursorUpdated = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null,
                TrailerEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{"1378"},
                null
        );

        TestUtilities.validateCursor("testUpdate. Error validating trailer entry table", trailerCursorUpdated, trailerValuesUpdated);
    }

}
