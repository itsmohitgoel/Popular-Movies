package com.mohit.popularmovies.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Bean containing information about movie objects
 * Created by Mohit on 15-05-2016.
 */
public class MovieItem implements Parcelable {
    // member variables
    private int  mMovieIdApi;
    private String mTitle;
    private String mPosterPath;
    private String mBackdropPath;
    private String mSummary;
    private String mReleaseDate;
    private String mRating;
    private float mPopularity;

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

    public int getMovieIdApi() { return mMovieIdApi; }

    public float getPopularity () { return mPopularity; }

    // Setters


    public void setMovieIdApi(int mMovieIdApi) { this.mMovieIdApi = mMovieIdApi; }

    public void setTitle(String mTitle) { this.mTitle = mTitle; }

    public void setBackdropPath(String mBackdropPath) { this.mBackdropPath = mBackdropPath; }

    public void setPosterPath(String mPosterPath) { this.mPosterPath = mPosterPath; }

    public void setSummary(String mSummary) { this.mSummary = mSummary; }

    public void setReleaseDate(String mReleaseDate) {
        //Show sample data if API has no info.
        if (mReleaseDate.equals("") || mTitle == null){
            this.mReleaseDate = "N/A";
            return;
        }
        this.mReleaseDate = mReleaseDate; }

    public void setRating(String mRating) { this.mRating = mRating; }

    public void setPopularity(float mPopularity) { this.mPopularity = mPopularity; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mMovieIdApi);
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mBackdropPath);
        dest.writeString(mSummary);
        dest.writeString(mReleaseDate);
        dest.writeString(mRating);
        dest.writeFloat(mPopularity);
    }

    // Movie constructor used by Parcel
    private MovieItem(Parcel source) {
        mMovieIdApi = source.readInt();
        mTitle = source.readString();
        mPosterPath = source.readString();
        mBackdropPath = source.readString();
        mSummary = source.readString();
        mReleaseDate = source.readString();
        mRating = source.readString();
        mPopularity = source.readFloat();
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
