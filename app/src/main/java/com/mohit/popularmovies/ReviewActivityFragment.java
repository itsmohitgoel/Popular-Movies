package com.mohit.popularmovies;

import android.app.ProgressDialog;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mohit.popularmovies.adapters.ReviewAdapter;
import com.mohit.popularmovies.beans.ReviewItem;
import com.mohit.popularmovies.listeners.IAsyncListener;
import com.mohit.popularmovies.utils.PopUtility;
import com.mohit.popularmovies.webservices.FetchReviewsAsync;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ReviewActivityFragment extends Fragment implements IAsyncListener{
    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_NAME = "movie_name";
    public final String REVIEW_KEY = "reviews";

    private ProgressDialog mProgressBar;
    private String mMovieId;
    private RecyclerView mRecyclerView;
    private ArrayList<ReviewItem> mReviewList;

    public ReviewActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieId = getArguments().getString(MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_review, container, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_review_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        if (savedInstanceState == null || !savedInstanceState.containsKey(REVIEW_KEY)) {
            mReviewList = new ArrayList<ReviewItem>();
            if (PopUtility.isNetworkConnected(getActivity())) {
                FetchReviewsAsync async = new FetchReviewsAsync(this);
                async.execute(mMovieId);
            }else {
                Toast.makeText(getContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        }else{
            mReviewList = savedInstanceState.getParcelableArrayList(REVIEW_KEY);
            onAsyncStop(mReviewList);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(REVIEW_KEY, mReviewList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAsyncBegin() {
        if (mProgressBar == null) {
            mProgressBar = ProgressDialog.show(getActivity(), "Loading", "Please wait...");
        }
    }

    @Override
    public void onAsyncEnd() {
        // Dummy implementation to fullfill contract
    }

    @Override
    public void onAsyncStop(Object object) {
        if (mProgressBar != null) {
            mProgressBar.cancel();
        }

        if (object instanceof ArrayList) {
            mReviewList = (ArrayList<ReviewItem>) object;
            if (mReviewList != null && mReviewList.size() > 0) {
                //TODO create and set adapter on recycler view
                ReviewAdapter adapter = new ReviewAdapter(getContext(), mReviewList);
                mRecyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(getContext(), getString(R.string.no_review), Toast.LENGTH_SHORT).show();;
            }
        }
    }
}
