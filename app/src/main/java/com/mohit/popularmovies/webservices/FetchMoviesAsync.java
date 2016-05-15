package com.mohit.popularmovies.webservices;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.mohit.popularmovies.beans.MovieItem;
import com.mohit.popularmovies.listeners.IAsyncListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Perform Networking operations
 * Created by Mohit on 15-05-2016.
 */
public class FetchMoviesAsync extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMoviesAsync.class.getSimpleName();
    public IAsyncListener mListener; //Listener to send data back to activity
    private ArrayList<MovieItem> moviesList; // To be updated via Jsonparsing

    public FetchMoviesAsync(IAsyncListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener argument can't be null");
        }
        mListener = listener;
    }

    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            //Make url and open connection
            Uri baseURI = Uri.parse("http://api.themoviedb.org/3/discover/movie?");
            Uri.Builder builder = baseURI.buildUpon()
                    .appendQueryParameter("api_key", "YOUR_API_KEY")
                    .appendQueryParameter("sort_by", "popularity.desc");
            Uri finalUri = builder.build();

            URL url = new URL(finalUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream is = urlConnection.getInputStream();
            if (is == null) {
                return null;
            }

            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);

            String line = "";
            StringBuilder bufferString = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                bufferString.append(line).append("\n");
            }

            if (bufferString.length() == 0) {
                return null;
            }
            String moviesJsonString = bufferString.toString();
            parseJson(moviesJsonString);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error in downloading");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mListener.onAsyncEnd(moviesList);
    }

    private void parseJson(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            moviesList = new ArrayList<>(jsonArray.length());
            MovieItem movie;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieJson = jsonArray.getJSONObject(i);
                movie = new MovieItem();
                movie.setTitle(movieJson.getString("title"));
                movie.setPosterPath(movieJson.getString("poster_path"));
                movie.setBackdropPath(movieJson.getString("backdrop_path"));
                movie.setSummary(movieJson.getString("overview"));
                movie.setReleaseDate(movieJson.getString("release_date"));
                movie.setRating(movieJson.getString("vote_average"));

                moviesList.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
