package com.mohit.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mohit.popularmovies.MoviesFragment;
import com.mohit.popularmovies.R;
import com.mohit.popularmovies.utils.PopConstants;
import com.squareup.picasso.Picasso;

/**
 * Custome Adapter to bind images to the gridview
 * Created by Mohit on 15-05-2016.
 */
public class MoviesAdapter extends CursorAdapter {
    private final String LOG_TAG = MoviesAdapter.class.getSimpleName();

    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        These views will be reused as needed
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_item_movie, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.grid_item_movie_imageView);
        String moviePosterURL = cursor.getString(MoviesFragment.COL_POSTER_PATH);

        Picasso.with(context).load(PopConstants.BASE_IMAGE_URL + moviePosterURL).into(imageView);
    }
}
