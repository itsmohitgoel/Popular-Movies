package com.mohit.popularmovies.webservices;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.mohit.popularmovies.beans.MovieItem;
import com.mohit.popularmovies.data.MovieContract.MovieEntry;
import com.mohit.popularmovies.listeners.IAsyncListener;
import com.mohit.popularmovies.utils.PopConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Perform Networking operations
 * Created by Mohit on 15-05-2016.
 */
public class FetchMoviesAsync extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMoviesAsync.class.getSimpleName();
    public IAsyncListener mListener; //Listener to send data back to activity
    private Context mContext;

    public FetchMoviesAsync(IAsyncListener listener, Context context) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener argument can't be null");
        }
        mListener = listener;
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mListener.onAsyncBegin();
    }

    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //Throw exception on missing api key
        if (PopConstants.API_KEY.equals("ADD Your API KEY HERE")) {
            throw new IllegalArgumentException("Please Enter your api key in PopConstants Class !");
        }
        try {
            //Make url and open connection
            Uri baseURI = Uri.parse(PopConstants.BASE_URL);
            Uri.Builder builder = baseURI.buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter(PopConstants.API_KEY_PARAM, PopConstants.API_KEY);
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
            mListener.onAsyncEnd();
    }
    private void parseJson(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            MovieItem movieItem;
            Vector<ContentValues> cvVector = new Vector<>(jsonArray.length());

            for(int i = 0; i <  jsonArray.length(); i++) {
                JSONObject movieJson = jsonArray.getJSONObject(i);

                int movieID = movieJson.getInt(PopConstants.MOVIE_API_ID);
                String originalTitle = movieJson.getString(PopConstants.ORIGINAL_TITLE);
                String overview = movieJson.getString(PopConstants.OVERVIEW);
                String releaseDate = movieJson.getString(PopConstants.RELEASE_DATE);
                String voteAverage = movieJson.getString(PopConstants.VOTE_AVERAGE);
                String posterPath = movieJson.getString(PopConstants.POSTER_PATH);
                String backdropPath = movieJson.getString(PopConstants.BACKDROP_PATH);
                float popularity = (float)movieJson.getDouble(PopConstants.POPULARITY);

                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movieID);
                movieValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
                movieValues.put(MovieEntry.COLUMN_OVERVIEW, overview);
                movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
                movieValues.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
                movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, backdropPath);
                movieValues.put(MovieEntry.COLUMN_POPULARITY, popularity);

                cvVector.add(movieValues);
            }

            //add all to database via bulkInsert call;
            int insertCount = 0;
            if (cvVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                cvVector.toArray(cvArray);
                insertCount = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);

            }
            Log.d(LOG_TAG, "inserted " + insertCount + " records in DB!");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
