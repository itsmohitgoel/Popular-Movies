package com.mohit.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mohit.popularmovies.R;
import com.mohit.popularmovies.data.MovieContract;
import com.mohit.popularmovies.utils.PopConstants;
import com.squareup.picasso.Picasso;

/** Custome Adapter to bind images to the gridview
 * Created by Mohit on 15-05-2016.
 */
public class MoviesAdapter extends CursorAdapter {
    private final String LOG_TAG = ArrayAdapter.class.getSimpleName();

    public MoviesAdapter(Context context, Cursor c , int flags) {
        super(context, c, flags);
    }

    /*
        These views will be reused as needed
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_item_movie, parent, false);

        Log.d(LOG_TAG, "Called newView()");
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.grid_item_movie_imageView);
        int moviePosterPathIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        String moviePosterURL = cursor.getString(moviePosterPathIndex);

        Picasso.with(context).load(PopConstants.BASE_IMAGE_URL + moviePosterURL).into(imageView);

        Log.d(LOG_TAG, "called bindView() with imageURL = '" + moviePosterURL + "'");
    }
}
