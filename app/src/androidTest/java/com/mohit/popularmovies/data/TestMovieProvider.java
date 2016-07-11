package com.mohit.popularmovies.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.sqlite.SQLiteDatabase;
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
}
