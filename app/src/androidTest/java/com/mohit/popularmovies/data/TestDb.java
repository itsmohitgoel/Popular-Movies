package com.mohit.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.mohit.popularmovies.data.MovieContract.MovieEntry;

import java.util.HashSet;

/**
 * Created by Mohit on 26-06-2016.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since I want to start with  a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    /* This function gets called before each test is executed to delete the database.
    * This makes sure that we always have a clean test.
    */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        This only test that the Movie table has the correct column names, regardless of the
        data it should contain.
     */
    public void testCreateDb() {
        // build a HashSet of all of the table names we wish to look for
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        deleteTheDatabase();
        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created  the database we want?
        Cursor dbCursor = db.rawQuery("SELECT name from sqlite_master where type='table'", null);
        assertTrue("Error: This means that database has not been created correctly", dbCursor.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(dbCursor.getString(0));
        } while (dbCursor.moveToNext());

        assertTrue("Error: Database was created without movie entry table", tableNameHashSet.isEmpty());

        // Now, do my tables contain correct columns?
        Cursor movieCursor = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + " )", null);
        assertTrue("Error: This means we were unable to query the database for table information", movieCursor.moveToFirst());


        // Build a HashSet of all the column names we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POPULARITY);

        int columnNameIndex = movieCursor.getColumnIndex("name");
        do {
            String columnName = movieCursor.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while (movieCursor.moveToNext());

        //If this fails that means my database doesn't contain all of the required Movie Entrly
        // columns
        assertTrue("Error: The database doesn't contain all of the required movie entry columns", movieColumnHashSet.isEmpty());


        Cursor trailerCursor = db.rawQuery("PRAGMA table_info(" + MovieContract.TrailerEntry.TABLE_NAME + " )", null);
        assertTrue("Error: This means we were unable to query the database for trailer table information", trailerCursor.moveToFirst());

        final HashSet<String> trailerColumnHashSet = new HashSet<>();
        trailerColumnHashSet.add(MovieContract.TrailerEntry._ID);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_MOVIE_ID);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_NAME);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_SITE);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_SIZE);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_TYPE);

        int trailerColumnNameIndex = trailerCursor.getColumnIndex("name");
        do {
            String trailerColumnName = trailerCursor.getString(trailerColumnNameIndex);
            trailerColumnHashSet.remove(trailerColumnName);
        } while (trailerCursor.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required trailer entry columns", trailerColumnHashSet.isEmpty());
        db.close();
    }

    /*
        Test by inserting and quering the movie table.
     */
    public void testMovieTable(){
        insertMovieData();
    }

    private long insertMovieData() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieValues();
        long movieRowId = TestUtilities.insertBackInTheDayMovieValues(mContext);

        Cursor movieCursor = db.query(MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null, null,
                null);

        assertTrue("Error: No records returned from the movie table", movieCursor.moveToFirst());
        TestUtilities.validateCurrentRecord(movieCursor, testValues);
        assertFalse("Error: More than one record returned from the movie query", movieCursor.moveToNext());

        movieCursor.close();
        db.close();
        return movieRowId;
    }

    /* Test by inserting and quering the trailer table */
    public void testTrailerTable() {
        long movieRowId = insertMovieData();

        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createTrailerValues(movieRowId);

        long trailerRowId = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, testValues);
        assertFalse("Error: Trailer not inserted Correctly", trailerRowId == -1);

        Cursor trailerCursor = db.query(MovieContract.TrailerEntry.TABLE_NAME,
                null,
                null,
                null,
                null, null,
                null);
        assertTrue("Error: No records returned from the table trailer", trailerCursor.moveToFirst());

        TestUtilities.validateCurrentRecord(trailerCursor, testValues);

        assertFalse("Error: More than one record from the trailer table", trailerCursor.moveToNext());

        trailerCursor.close();
        db.close();
        dbHelper.close();
    }
}
