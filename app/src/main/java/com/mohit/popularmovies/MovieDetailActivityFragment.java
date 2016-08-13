package com.mohit.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mohit.popularmovies.data.MovieContract.MovieEntry;
import com.mohit.popularmovies.utils.PopConstants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ProgressDialog mProgressBar;
    public static final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();
    private static final int MOVIES_LOADER = 0;

    //specify the columns required and utilize the Projection
    private static final String[] MOVIE_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_BACKDROP_PATH
    };

    private static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_TITLE = 1;
    private static final int COL_MOVIE_OVERVIEW = 2;
    private static final int COL_MOVIE_RELEASE_DATE = 3;
    private static final int COL_MOVIE_VOTE_AVERAGE = 4;
    private static final int COL_MOVIE_BACKDROP_PATH = 5;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LoaderManager lm = getLoaderManager();
        lm.initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null || intent.getData() == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of creating
        // a Cursor for the data being displayed.
        Uri movieDetailUri = intent.getData();
        CursorLoader cLoader = new CursorLoader(
                getActivity(),
                movieDetailUri,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
        return cLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        TextView vTitle = (TextView) getView().findViewById(R.id.textView_movie_title);
        TextView vsummary = (TextView) getView().findViewById(R.id.textview_movie_summary);
        TextView vDate = (TextView) getView().findViewById(R.id.textView_movie_release_date);
        TextView vRating = (TextView) getView().findViewById(R.id.textview_move_rating);
        ImageView vPoster = (ImageView) getView().findViewById(R.id.imageView_poster);

        String title = data.getString(COL_MOVIE_TITLE);
        String overview = data.getString(COL_MOVIE_OVERVIEW);
        String releaseDate = data.getString(COL_MOVIE_RELEASE_DATE);
        String rating = data.getString(COL_MOVIE_VOTE_AVERAGE);
        String backdropPath = data.getString(COL_MOVIE_BACKDROP_PATH);

        vTitle.setText(title);
        vsummary.setText(overview);
        vDate.setText(releaseDate);
        vRating.setText(rating);
        if (mProgressBar == null) {
            mProgressBar = ProgressDialog.show(getActivity(), "Loading", "Please wait....");
        }
        if (!backdropPath.equals("null")) {
            Picasso.with(getActivity()).load(PopConstants.BASE_IMAGE_URL + backdropPath).into(vPoster, new Callback() {
                @Override
                public void onSuccess() {
                    if (mProgressBar != null) {
                        mProgressBar.cancel();
                    }
                }

                @Override
                public void onError() {

                }
            });
        } else {
            Picasso.with(getActivity()).load(R.drawable.not_availaible).into(vPoster, new Callback() {
                @Override
                public void onSuccess() {
                    if (mProgressBar != null) {
                        mProgressBar.cancel();
                    }
                }

                @Override
                public void onError() {

                }
            });
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
