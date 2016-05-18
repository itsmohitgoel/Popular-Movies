package com.mohit.popularmovies;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mohit.popularmovies.beans.MovieItem;
import com.mohit.popularmovies.utils.PopConstants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {
    private ProgressDialog mProgressBar;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        MovieItem movie = getActivity().getIntent().getParcelableExtra(PopConstants.MOVIE_KEY);

        TextView vTitle = (TextView) rootView.findViewById(R.id.textView_movie_title);
        TextView vDate = (TextView) rootView.findViewById(R.id.textView_movie_release_date);
        TextView vRating = (TextView) rootView.findViewById(R.id.textview_move_rating);
        TextView vsummary = (TextView) rootView.findViewById(R.id.textview_movie_summary);
        ImageView vPoster = (ImageView) rootView.findViewById(R.id.imageView_poster);

        vTitle.setText(movie.getTitle());
        vDate.setText(movie.getReleaseDate());
        vRating.setText(String.format("%s / 10", movie.getRating()));
        vsummary.setText(movie.getSummary());
        if (mProgressBar == null) {
            mProgressBar = ProgressDialog.show(getContext(), "Loading", "Please wait...");
        }
        if (!movie.getBackdropPath().equalsIgnoreCase("null")) {
            Picasso.with(getContext()).load(PopConstants.BASE_IMAGE_URL + movie.getBackdropPath()).into(vPoster, new Callback() {
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
            Picasso.with(getContext()).load(R.drawable.not_availaible).into(vPoster, new Callback() {
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

        return rootView;
    }
}
