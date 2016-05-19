package com.mohit.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mohit.popularmovies.R;
import com.mohit.popularmovies.beans.MovieItem;
import com.mohit.popularmovies.utils.PopConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/** Custome Adapter to bind images to the gridview
 * Created by Mohit on 15-05-2016.
 */
public class MoviesAdapter extends ArrayAdapter<MovieItem> {
    private Context mContext;
    private List<MovieItem> moviesList;
    private final String LOG_TAG = ArrayAdapter.class.getSimpleName();

    public MoviesAdapter(Context context, List<MovieItem> movieItemList) {
        super(context, 0, movieItemList);
        this.mContext = context;
        this.moviesList = movieItemList;
    }

    public void setMoviesList(ArrayList<MovieItem> mList) {
        this.moviesList = mList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return moviesList.size();
    }

    @Override
    public MovieItem getItem(int position) {
        return moviesList.get(position);
    }

    //ViewHolder class for smoother scrolling
    static class ViewHolder{
        ImageView imageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {  //First time, inflate the container view
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(R.layout.grid_item_movie, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_movie_imageView);
            row.setTag(holder);
        }else{  //second time, re-utilize the recycler view
            holder = (ViewHolder) row.getTag();
        }

        MovieItem item = moviesList.get(position);
        if (!item.getPosterPath().equalsIgnoreCase("null")) {
            Picasso.with(mContext).load(PopConstants.BASE_IMAGE_URL + item.getPosterPath()).into(holder.imageView);
        }else{ // If API returns null for the poster image, display sample image.
            Picasso.with(mContext).load(R.drawable.not_availaible).into(holder.imageView);
        }
        return row;
    }
}
