package com.mohit.popularmovies;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.mohit.popularmovies.adapters.MoviesAdapter;
import com.mohit.popularmovies.beans.MovieItem;
import com.mohit.popularmovies.data.MovieContract;
import com.mohit.popularmovies.listeners.IAsyncListener;
import com.mohit.popularmovies.utils.PopConstants;
import com.mohit.popularmovies.utils.PopUtility;
import com.mohit.popularmovies.webservices.FetchMoviesAsync;
import com.mohit.popularmovies.webservices.FetchTrailersAsync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment implements IAsyncListener, LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private GridView mGridView;
    private MoviesAdapter mMoviesAdapter; //Custom Adapter for movies data
    private ArrayList<MovieItem> mGridList;
    private ProgressDialog mProgressBar;
    private static final int MOVIES_LOADER = 0;

    // For the Movies view, we are showing only a small subset of stored data,
    // specify the columns we need.
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_POSTER_PATH = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey(PopConstants.MOVIES_KEY)) {
            mGridList = new ArrayList<MovieItem>();
        } else {
            mGridList = savedInstanceState.getParcelableArrayList(PopConstants.MOVIES_KEY);
        }
        setHasOptionsMenu(true);
    }

    public MoviesFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LoaderManager lm = getLoaderManager();
        lm.initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        //Initialize the adapter

        // The CursorAdapter will take the data from cursor and populate the GridView
        // However, we can't use the FLAG_AURO_QUERY since its deprecated, so we will
        // end up with empty grid the first time we run.
        mMoviesAdapter = new MoviesAdapter(getActivity(), null, 0);
        mGridView.setAdapter(mMoviesAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    long movieID = cursor.getLong(COL_MOVIE_ID);
                    Uri movieSelectedUri = MovieContract.MovieEntry.buildMovieUri(movieID);
                    ICallback callbackObject = (ICallback) getActivity();
                    callbackObject.onMovieselected(movieSelectedUri);
                }
            }
        });
        return rootView;
    }

    /**
     * Download Movies,Trailers via AsyncTask, after checking the
     * n/w status, and getting user defined sort order settings
     */
    private void updateMoviesData() {
        if (PopUtility.isNetworkConnected(getActivity())) {
            FetchMoviesAsync moviesAsync = new FetchMoviesAsync(this, getActivity());
            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort_by_preference = mPreferences.getString(getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_default));
            moviesAsync.execute(sort_by_preference);

            // In order to launch FetchTrailersAsync task, need
            // to do  a query first, in order to obtain list of movie ids
            String[] columns = {MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE_ID};
            Cursor cursor = getActivity().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    columns,
                    null,
                    null,
                    null
            );

            Map<String, String> moviesIdMap = getMoviesIdMap(cursor);

            // Starting HONEYCOMB, I know asynctask will run sequentially one after other,
            // so I don't need to write code for manual synchronization
            FetchTrailersAsync trailersAsync = new FetchTrailersAsync(this, getActivity());
            trailersAsync.execute(moviesIdMap);
        }
    }

    private Map<String,String> getMoviesIdMap(Cursor cursor) {
        Map<String, String> movieIdsMap = new HashMap<>(cursor.getCount());

        int idxMovieRowID = cursor.getColumnIndex(MovieContract.MovieEntry._ID);
        int idxMovieApiID = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);

        while (cursor.moveToNext()) {
            String movieRowID = cursor.getString(idxMovieRowID);
            String movieApiID = cursor.getString(idxMovieApiID);
            movieIdsMap.put(movieRowID, movieApiID);
        }

        return  movieIdsMap;
    }


    @Override
    public void onAsyncBegin() {
        Log.d(LOG_TAG, "on Async Begin");
        if (mProgressBar == null) {
            mProgressBar = ProgressDialog.show(getActivity(), "Loading", "Please wait...");
            Log.d(LOG_TAG, "Progress Begin");
        }
    }

    @Override
    public void onAsyncEnd() {
        Log.d(LOG_TAG, "on Async End");
        if (mProgressBar != null) {
            mProgressBar.cancel();
            mProgressBar = null;
            Log.d(LOG_TAG, "Progress End");
        }
    }

    @Override
    public void onAsyncStop(Object object) {
        //Dummy implemention to fullfill interface contract
    }

    /**
     * Return the sorting order to in terms of db 'movie' table
     * column name.
     */
    private String normalizeSortingOrder() {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by_preference = mPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));

        String sortOrderDB = "";
        if (sort_by_preference.equals(getString(R.string.pref_sort_by_popularity_value))) {
            sortOrderDB = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        } else if(sort_by_preference.equals(getString(R.string.pref_sort_by_rating_value))){
            sortOrderDB = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        } else if (sort_by_preference.equals(getString(R.string.pref_sort_by_favourite_value))) {
            sortOrderDB = MovieContract.MovieEntry.COLUMN_FAVOURITE + " DESC";
        }
        return sortOrderDB;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = normalizeSortingOrder();
        String selectionClause = null;
        String[] selectionArgs = null;
        if (sortOrder.equals(MovieContract.MovieEntry.COLUMN_FAVOURITE + " DESC")) {
            selectionClause = MovieContract.MovieEntry.COLUMN_FAVOURITE + " = ?";
            selectionArgs = new String[]{"TRUE"};
        }
        CursorLoader cLoader = new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                selectionClause,
                selectionArgs,
                sortOrder);
        return cLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "No. of movies retrieved from db by onLoadFinished() : " + data.getCount());
        mMoviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }

    public void onSortingChaged() {
        updateMoviesData();
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            updateMoviesData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selection
     */
    public interface ICallback {
        /**
         * DetailFragment callback for when an item is selected
         *
         * @param movieUri containing row id of movie in local db i.e '_id'
         */
        public void onMovieselected(Uri movieUri);
    }
}
