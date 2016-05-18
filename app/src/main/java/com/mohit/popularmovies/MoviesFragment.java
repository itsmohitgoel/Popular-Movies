package com.mohit.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.mohit.popularmovies.adapters.MoviesAdapter;
import com.mohit.popularmovies.beans.MovieItem;
import com.mohit.popularmovies.listeners.IAsyncListener;
import com.mohit.popularmovies.utils.PopConstants;
import com.mohit.popularmovies.utils.PopUtility;
import com.mohit.popularmovies.webservices.FetchMoviesAsync;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment implements IAsyncListener {
    private final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private GridView mGridView;
    private MoviesAdapter mAdapter; //Custom Adapter for movies data
    private ArrayList<MovieItem> mGridList;
    private ProgressDialog mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey(PopConstants.MOVIES_KEY)) {
            mGridList =  new ArrayList<MovieItem>();
        }else {
            mGridList = savedInstanceState.getParcelableArrayList(PopConstants.MOVIES_KEY);
        }
    }

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        mAdapter = new MoviesAdapter(getActivity(), mGridList);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieItem movie = mAdapter.getItem(position);
                Intent intentMovie = new Intent(getActivity(), MovieDetailActivity.class);
                intentMovie.putExtra(PopConstants.MOVIE_KEY, movie);
                startActivity(intentMovie);
            }
        });

        return rootView;
    }

    private void updateMoviesData() {
        if (PopUtility.isNetworkConnected(getActivity())) {
            FetchMoviesAsync moviesAsync = new FetchMoviesAsync(this);
            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort_by_preference = mPreferences.getString(getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_default));
            moviesAsync.execute(sort_by_preference);
        }
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
    public void onAsyncEnd(List<MovieItem> movieList) {
            Log.d(LOG_TAG, "on Async End");
        if(mProgressBar != null){
            mProgressBar.cancel();
            mProgressBar = null;
            Log.d(LOG_TAG, "Progress End");
        }
        mGridList = (ArrayList<MovieItem>) movieList;
        mAdapter.clear();
        mAdapter.setMoviesList((ArrayList<MovieItem>) movieList);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMoviesData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(PopConstants.MOVIES_KEY, mGridList);
        super.onSaveInstanceState(outState);
    }
}
