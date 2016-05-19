package com.mohit.popularmovies.listeners;

import com.mohit.popularmovies.beans.MovieItem;

import java.util.List;

/**
 * Interface with callback methods for communication  b/w AsyncTask
 * and fragments.
 * Created by Mohit on 15-05-2016.
 */
public interface IAsyncListener {
    /**
     * Called on Activities/Fragments to communicate
     * with AsyncTask, and update its UI, before
     * execution begins
     */
    public abstract void onAsyncBegin();

    /**
     * Called mostly on Fragment object, to update its
     * UI, When Async Finish executing.
     * @param movieList
     */
    public abstract void onAsyncEnd(List<MovieItem> movieList);
}
