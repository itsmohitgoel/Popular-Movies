package com.mohit.popularmovies.beans;

/** Bean containing information
 * about movie
 * Created by Mohit on 15-05-2016.
 */
public class MovieItem {
    private String posterURL;

    public  MovieItem(){
        super();
    }
    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }
}
