package com.mohit.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Mohit on 05-07-2016.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private MovieDbHelper mMovieDbHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int TRAILER = 200;
    static final int TRAILER_WITH_MOVIE = 201;

    private static final SQLiteQueryBuilder mMovieQueryBuilder;
    private static final SQLiteQueryBuilder mMovieAndTrailerQueryBuilder;

    static {
        mMovieQueryBuilder = new SQLiteQueryBuilder();

        //This is a simple movie query builder, without trailer informartion
        mMovieQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);

        //This query builder must perform inner join b/w two tables
        //NOTE: for each trailer only one movie is present, but for single
        // movie, there can be multiple trailers availaible
        mMovieAndTrailerQueryBuilder = new SQLiteQueryBuilder();
        mMovieAndTrailerQueryBuilder.setTables(
                MovieContract.TrailerEntry.TABLE_NAME + " INNER JOIN " + MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID);
    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE_ID, MOVIE_WITH_ID);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILER + "/#", TRAILER_WITH_MOVIE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = mUriMatcher.match(uri);
        SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (match) {
            case MOVIE:
                cursor = mMovieQueryBuilder.query(db,
                        projection,
                        null,
                        null,
                        null, null,
                        sortOrder
                );
                break;
            case MOVIE_WITH_ID:
                mMovieQueryBuilder.appendWhere(
                        MovieContract.MovieEntry._ID + " = " + uri.getPathSegments().get(1));
                cursor = mMovieQueryBuilder.query(db,
                        projection,
                        null,
                        null,
                        null, null,
                        sortOrder);
                break;
            case TRAILER:
                cursor = db.query(MovieContract.TrailerEntry.TABLE_NAME, projection, null, null, null, null, null, sortOrder);
                break;
            case TRAILER_WITH_MOVIE:
                long movieID = MovieContract.TrailerEntry.getMovieIdFromTrailerUri(uri);
                String trailerSelection = MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?";
                cursor = mMovieAndTrailerQueryBuilder.query(db,
                        projection,
                        trailerSelection,
                        new String[]{Long.toString(movieID)},
                        null, null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("unknown uri in query() method: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case TRAILER_WITH_MOVIE:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

}
