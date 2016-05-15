package com.mohit.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.mohit.popularmovies.adapters.MoviesAdapter;
import com.mohit.popularmovies.beans.MovieItem;
import com.mohit.popularmovies.listeners.IAsyncListener;
import com.mohit.popularmovies.webservices.FetchMoviesAsync;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment implements IAsyncListener{
    private final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private GridView mGridView;
    private MoviesAdapter mAdapter;
    private ArrayList<MovieItem> mGridList;
    private String TEST_URL = "http://javatechig.com/?json=get_recent_posts&count=45";

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        mGridList = new ArrayList<MovieItem>();
        mAdapter = new MoviesAdapter(getActivity(), mGridList);
        mGridView.setAdapter(mAdapter);

        //Start download
        FetchMoviesAsync moviesAsync = new FetchMoviesAsync(mGridList);
        moviesAsync.mListener = this;
        moviesAsync.execute(TEST_URL);

        return rootView;
    }

    @Override
    public void onAsyncEnd(List<MovieItem> movieList) {
        mAdapter.setMoviesList((ArrayList<MovieItem>) movieList);
    }
}
