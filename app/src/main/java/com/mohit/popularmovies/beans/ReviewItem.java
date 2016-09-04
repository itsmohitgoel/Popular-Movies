package com.mohit.popularmovies.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mohit on 02-09-2016.
 */
public class ReviewItem implements Parcelable {
    private String mContent;
    private String mAuthor;
    private String mReviewId;

    // Getters
    public String getContent() { return mContent; }

    public String getAuthour() { return mAuthor; }

    public String getReviewId() { return mReviewId; }

    // Setters
    public void setContent(String content) { this.mContent = content; }

    public void setAuthour(String authour) { this.mAuthor = authour; }

    public void setReviewId(String reviewId) { this.mReviewId = reviewId; }

    @Override
    public int describeContents() {
        return 0;
    }

    public ReviewItem() {
        super();
    }

    ;

    public ReviewItem(Parcel source) {
        mContent = source.readString();
        mAuthor = source.readString();
        mReviewId = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mContent);
        dest.writeString(mAuthor);
        dest.writeString(mReviewId);
    }

    public static final Parcelable.Creator<ReviewItem> CREATOR = new Parcelable.Creator<ReviewItem>() {

        @Override
        public ReviewItem createFromParcel(Parcel source) {
            return new ReviewItem(source);
        }

        @Override
        public ReviewItem[] newArray(int size) {
            return new ReviewItem[0];
        }
    };
}
