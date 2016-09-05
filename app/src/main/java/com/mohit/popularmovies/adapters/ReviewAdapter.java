package com.mohit.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mohit.popularmovies.R;
import com.mohit.popularmovies.beans.ReviewItem;

import java.util.ArrayList;

/**
 * Created by Mohit on 02-09-2016.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ReviewItem> mReviewsList;

    public ReviewAdapter(Context context, ArrayList<ReviewItem> list) {
        this.mContext = context;
        this.mReviewsList = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView authorView;
        TextView reviewView;

        public ViewHolder(View itemView) {
            super(itemView);
            authorView = (TextView) itemView.findViewById(R.id.textview_author);
            reviewView = (TextView) itemView.findViewById(R.id.textview_review);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_review_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.authorView.setText(mReviewsList.get(position).getAuthour());
        holder.reviewView.setText(mReviewsList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mReviewsList.size();
    }
}
