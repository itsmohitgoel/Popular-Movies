package com.mohit.popularmovies.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Bean containing information about movie objects
 * Created by Mohit on 15-05-2016.
 */
public class MovieItem implements Parcelable {
    // member variables
    private String mTitle;
    private String mPosterPath;
    private String mBackdropPath;
    private String mSummary;
    private String mReleaseDate;
    private String mRating;


    //MovieItem Constructor
    public MovieItem() {
        super();
    }

    // Getters
    public String getTitle() { return mTitle; }

    public String getBackdropPath() { return mBackdropPath; }

    public String getPosterPath() { return mPosterPath; }

    public String getSummary() { return mSummary; }

    public String getReleaseDate() { return mReleaseDate; }

    public String getRating() { return mRating; }

    // Setters
    public void setTitle(String mTitle) { this.mTitle = mTitle; }

    public void setBackdropPath(String mBackdropPath) { this.mBackdropPath = mBackdropPath; }

    public void setPosterPath(String mPosterPath) { this.mPosterPath = mPosterPath; }

    public void setSummary(String mSummary) { this.mSummary = mSummary; }

    public void setReleaseDate(String mReleaseDate) { this.mReleaseDate = mReleaseDate; }

    public void setRating(String mRating) { this.mRating = mRating; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mBackdropPath);
        dest.writeString(mSummary);
        dest.writeString(mReleaseDate);
        dest.writeString(mRating);
    }

    // Movie constructor used by Parcel
    private MovieItem(Parcel source) {
        mTitle = source.readString();
        mPosterPath = source.readString();
        mBackdropPath = source.readString();
        mSummary = source.readString();
        mReleaseDate = source.readString();
        mRating = source.readString();
    }

        public static final Parcelable.Creator<MovieItem> CREATOR =
                new Parcelable.Creator<MovieItem>(){

                    @Override
                    public MovieItem createFromParcel(Parcel source) {
                        return new MovieItem(source);
                    }

                    @Override
                    public MovieItem[] newArray(int size) {
                        return new MovieItem[0];
                    }
                };

    @Override
    public String toString() {
        return "Title: " + mTitle;
    }
}
