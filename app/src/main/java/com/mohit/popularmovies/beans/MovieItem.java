package com.mohit.popularmovies.beans;

/**
 * Bean containing information about movie objects
 * Created by Mohit on 15-05-2016.
 */
public class MovieItem {
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

    public String getPoster() { return mBackdropPath; }

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

}
