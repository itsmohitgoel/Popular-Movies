package com.mohit.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mohit.popularmovies.R;
import com.mohit.popularmovies.beans.MovieItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohit on 15-05-2016.
 */
public class MoviesAdapter extends ArrayAdapter<MovieItem> {
    private Context mContext;
    private int layoutResourceId;
    private List<MovieItem> moviesList;

    public MoviesAdapter(Context context, List<MovieItem> movieItemList) {
        super(context, 0, movieItemList);
        this.mContext = context;
        this.moviesList = movieItemList;
    }

    public void setMoviesList(ArrayList<MovieItem> mList) {
        this.moviesList = mList;
        notifyDataSetChanged();
    }
    static class ViewHolder{
        ImageView imageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(R.layout.grid_item_movie, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_movie_imageView);
            row.setTag(holder);
        }else{
            holder = (ViewHolder) row.getTag();
        }

        MovieItem item = moviesList.get(position);
        Picasso.with(mContext).load(item.getPosterURL()).into(holder.imageView);
        return row;
    }
}
