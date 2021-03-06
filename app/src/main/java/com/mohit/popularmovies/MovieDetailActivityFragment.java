package com.mohit.popularmovies;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mohit.popularmovies.data.MovieContract.MovieEntry;
import com.mohit.popularmovies.data.MovieContract.TrailerEntry;
import com.mohit.popularmovies.utils.PopConstants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ProgressDialog mProgressBar;
    private String mMovieApiId;
    public static final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();
    private static final int MOVIES_LOADER = 0;
    private static final int TRAILER_LOADER = 1;
    private TrailerLoaderCallbacks mtrailerCallback = new TrailerLoaderCallbacks();

    public static final String DETAIL_URI = "URI";
    private Uri mUri;
    private LinearLayout mTrailerContainer;

    //specify the columns required and utilize the Projection
    private static final String[] MOVIE_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_BACKDROP_PATH,
            MovieEntry.COLUMN_FAVOURITE,
            MovieEntry.COLUMN_MOVIE_ID,
    };

    private static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_TITLE = 1;
    private static final int COL_MOVIE_OVERVIEW = 2;
    private static final int COL_MOVIE_RELEASE_DATE = 3;
    private static final int COL_MOVIE_VOTE_AVERAGE = 4;
    private static final int COL_MOVIE_BACKDROP_PATH = 5;
    private static final int COL_MOVIE_FAVOURITE = 6;
    public static final int COL_MOVIE_API_ID = 7;

    //specify the columns for Trailer and define respecitive Projections
    private static final String[] TRAILER_COLUMNS = {
            TrailerEntry.TABLE_NAME + "." + TrailerEntry._ID,
            TrailerEntry.COLUMN_MOVIE_ID,
            TrailerEntry.COLUMN_NAME,
            TrailerEntry.COLUMN_SITE,
            TrailerEntry.COLUMN_TRAILER_KEY,
            TrailerEntry.COLUMN_SIZE,
            TrailerEntry.COLUMN_TYPE
    };
    private static final int COL_TRAILER_ID = 0;
    private static final int COL_TRAILER_MOVIE_ID = 1;
    private static final int COL_TRAILER_NAME = 2;
    private static final int COL_TRAILER_SITE = 3;
    private static final int COL_TRAILER_KEY = 4;
    private static final int COL_TRAILER_SIZE = 5;
    private static final int COL_TRAILER_TYPE = 6;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle argsBundle = getArguments();
        if (argsBundle != null) {
            mUri = argsBundle.getParcelable(MovieDetailActivityFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mTrailerContainer = (LinearLayout) rootView.findViewById(R.id.trailers_container);
        if (savedInstanceState != null) {
            getLoaderManager().restartLoader(TRAILER_LOADER, null, mtrailerCallback);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LoaderManager lm = getLoaderManager();
        lm.initLoader(MOVIES_LOADER, null, this);
        lm.initLoader(TRAILER_LOADER, null, mtrailerCallback);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of creating
        // a Cursor for the data being displayed.
        CursorLoader cLoader = null;
        if (mUri != null) {
            cLoader = new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
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
        final Button buttonFavourite = (Button) getView().findViewById(R.id.button_mark_as_favourite);
        Button btnReview = (Button) getView().findViewById(R.id.button_review);

        final String movieRowId = data.getString(COL_MOVIE_ID);
        String title = data.getString(COL_MOVIE_TITLE);
        String overview = data.getString(COL_MOVIE_OVERVIEW);
        String releaseDate = data.getString(COL_MOVIE_RELEASE_DATE);
        String rating = data.getString(COL_MOVIE_VOTE_AVERAGE);
        String backdropPath = data.getString(COL_MOVIE_BACKDROP_PATH);
        final boolean isFavourite = ((data.getString(COL_MOVIE_FAVOURITE)).equalsIgnoreCase("TRUE") ? true : false);
        mMovieApiId = data.getString(COL_MOVIE_API_ID);

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

        if (isFavourite) {
            buttonFavourite.setText(getString(R.string.unfavourite));
        } else {
            buttonFavourite.setText(getString(R.string.mark_as_favourite_text));
        }


        // update movie to favourite in local db, when favourite button is clicked
        buttonFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues updatedValues = new ContentValues();
                Toast toast;
                String buttonLabel;

                // If movie already marked favourite, change button label,
                // contentvalues, and toast message
                if (isFavourite) {
                    buttonLabel = getString(R.string.unfavourite);
                    buttonFavourite.setText(buttonLabel);
                    buttonLabel = getString(R.string.mark_as_favourite_text);
                    updatedValues.put(MovieEntry.COLUMN_FAVOURITE, "FALSE");
                    toast = Toast.makeText(getActivity(), R.string.marked_as_unfavourite, Toast.LENGTH_SHORT);
                } else {
                    buttonLabel = getString(R.string.mark_as_favourite_text);
                    buttonFavourite.setText(buttonLabel);
                    buttonLabel = getString(R.string.unfavourite);
                    updatedValues.put(MovieEntry.COLUMN_FAVOURITE, "TRUE");
                    toast = Toast.makeText(getActivity(), R.string.marked_as_favourite, Toast.LENGTH_SHORT);
                }

                String selectionClause = MovieEntry._ID + " = ?";
                String[] selectionArgs = new String[]{movieRowId};

                int count = getActivity().getContentResolver().update(
                        MovieEntry.CONTENT_URI,
                        updatedValues,
                        selectionClause,
                        selectionArgs
                );

                if (count > 0) {
                    buttonFavourite.setText(buttonLabel);
                    toast.show();
                }
            }
        });

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReviewClick(v);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class TrailerLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // First need to construct trailer uri with movie id
            CursorLoader cLoader = null;
            if (mUri != null) {
                long movieUri = MovieEntry.getMovieIdFromMovieUri(mUri);
                cLoader = new CursorLoader(getActivity(),
                        TrailerEntry.buildTrailerUriWithMovieId(movieUri),
                        TRAILER_COLUMNS,
                        null,
                        null,
                        null);
            }
            return cLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.getCount() == 0) {
                return;
            }
            while (data.moveToNext()) {
                Log.d(LOG_TAG, "movie_id : " + data.getString(0));
                View trailerLayout = getActivity().getLayoutInflater().inflate(R.layout.view_trailer_item, null);
                ImageView imageView = (ImageView) trailerLayout.findViewById(R.id.trailer_icon);
                TextView textView = (TextView) trailerLayout.findViewById(R.id.trailer_label);

                String trailerName = data.getString(COL_TRAILER_NAME);
                final String trailerKey = data.getString(COL_TRAILER_KEY);

                textView.setText(trailerName);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri trailerUri = Uri.parse(PopConstants.YOUTUBE_VIDEO_URL).buildUpon().appendQueryParameter(
                                PopConstants.YOUTUBE_VIDEO_PARAM, trailerKey
                        ).build();
                        Intent intent = new Intent(Intent.ACTION_VIEW, trailerUri);
                        startActivity(intent);
                    }
                });

                mTrailerContainer.addView(trailerLayout);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    public void onReviewClick(View view) {
        Intent intent = new Intent(getActivity(), ReviewActivity.class);
        intent.putExtra(ReviewActivityFragment.MOVIE_ID, mMovieApiId);
        startActivity(intent);
    }
}
