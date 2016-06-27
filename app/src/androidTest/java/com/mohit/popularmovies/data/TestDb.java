package com.mohit.popularmovies.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

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
        Cursor c = db.rawQuery("SELECT name from sqlite_master where type='table'", null);
        assertTrue("Error: This means that database has not been created correctly", c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: Database was created without movie entry table", tableNameHashSet.isEmpty());

        // Now, do my tables contain correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + " )", null);
        assertTrue("Error: This means we were unable to query the database for table information", c.moveToFirst());

        // Build a HashSet of all the column names we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        //If this fails that means my database doesn't contain all of the required Movie Entrly
        // columns
        assertTrue("Error: The database doesn't contain all of the required movie entry columns", movieColumnHashSet.isEmpty());
        db.close();
    }
}
