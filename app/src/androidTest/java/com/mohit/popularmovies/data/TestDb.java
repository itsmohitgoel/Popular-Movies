package com.mohit.popularmovies.data;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Mohit on 26-06-2016.
 */
public class TestDb extends AndroidTestCase{
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since I want to start with  a clean slate
    void deleteTheDatabase(){
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
    public void testCreateDb(){
        // build a HashSet of all of the table names we wish to look for
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        deleteTheDatabase();
        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
    }
}
