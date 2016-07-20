package com.mohit.popularmovies.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Bean containing information about trailer objects
 * Created by Mohit on 18-07-2016.
 */
public class TrailerItem implements Parcelable {
    private int mMovieId;
    private String mName;
    private String mSite;
    private String mTrailerKey;
    private int mSize;
    private String mType;

    // Getters
    public int getMovieId() {
        return mMovieId;
    }

    public String getName() {
        return mName;
    }

    public String getSite() {
        return mSite;
    }

    public String getTrailerKey() {
        return mTrailerKey;
    }

    public int getSize() {
        return mSize;
    }

    public String getType() {
        return mType;
    }

    // Setters
    public void setMovieId(int mMovieId) {
        this.mMovieId = mMovieId;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setSite(String mSite) {
        this.mSite = mSite;
    }

    public void setTrailerKey(String mTrailerKey) {
        this.mTrailerKey = mTrailerKey;
    }

    public void setSize(int mSize) {
        this.mSize = mSize;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mMovieId);
        dest.writeString(mName);
        dest.writeString(mSite);
        dest.writeString(mTrailerKey);
        dest.writeInt(mSize);
        dest.writeString(mType);
    }

    // Trailer constructor used by Parcel
    private TrailerItem(Parcel source) {
        mMovieId = source.readInt();
        mName = source.readString();
        mSite = source.readString();
        mTrailerKey = source.readString();
        mSize = source.readInt();
        mType = source.readString();
    }

    public static final Parcelable.Creator<TrailerItem> CREATOR =
            new Parcelable.Creator<TrailerItem>() {

                @Override
                public TrailerItem createFromParcel(Parcel source) {
                    return new TrailerItem(source);
                }

                @Override
                public TrailerItem[] newArray(int size) {
                    return new TrailerItem[0];
                }
            };

    @Override
    public String toString() {
        return "Trailer Name: " + mName;
    }
}
