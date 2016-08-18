package com.mohit.popularmovies.webservices;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.mohit.popularmovies.data.MovieContract.TrailerEntry;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Vector;

/**
 * Download Trailers for the given list of movie ids
 * Created by Mohit on 19-08-2016.
 */
public class FetchTrailersAsync extends AsyncTask<Map<String, String>, Void, Void> {
    public static final String LOG_TAG = FetchTrailersAsync.class.getSimpleName();
    public IAsyncListener mTrailerCallback; //callback object(or listener) to be notified of completion
    private Context mContext;

    public FetchTrailersAsync(IAsyncListener listener, Context context) {
        mTrailerCallback = listener;
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //TODO notify callback object
    }

    @Override
    protected Void doInBackground(Map<String, String>... params) {
        //params is an var-arg array of different movie ids
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //Throw exception on missing api key
        if (PopConstants.API_KEY.equals("ADD Your API KEY HERE")) {
            throw new IllegalArgumentException("Please Enter your api key in PopConstants Class !");
        }

        for (Map.Entry<String, String>  entry: params[0].entrySet()) {
            long movieRowId = Long.parseLong(entry.getKey());
            String movieId = entry.getValue();
            if (movieId == null || movieId.equals("")) {
                continue;
            }
            try {
                // Make url and open connection
                Uri baseURI = Uri.parse(PopConstants.BASE_URL);
                Uri.Builder builder = baseURI.buildUpon()
                        .appendPath(movieId)
                        .appendPath(PopConstants.TRAILER_PATH)
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

                if ((line = reader.readLine()) != null) {
                    bufferString.append(line).append("\n");
                }

                if (bufferString.length() == 0) {
                    return null;
                }

                String trailerJsonString = bufferString.toString();
                parseJson(trailerJsonString, movieRowId);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "URL is not correct");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error in Trailer downloading");
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error closing stream");
                    }
                }
            }
        }
        return null;
    }

    private void parseJson(String trailerJsonString, long movieRowId) {
        try {
            JSONObject jsonObject = new JSONObject(trailerJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(PopConstants.RESULTS);
            int movieApiId = jsonObject.getInt(PopConstants.MOVIE_API_ID);

            Vector<ContentValues> cvVector = new Vector<>(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject trailerJson = jsonArray.getJSONObject(i);
                String name = trailerJson.getString(PopConstants.TRAILER_NAME);
                String site = trailerJson.getString(PopConstants.TRAILER_SITE);
                String key = trailerJson.getString(PopConstants.TRAILER_KEY);
                int size = trailerJson.getInt(PopConstants.TRAILER_SIZE);
                String type = trailerJson.getString(PopConstants.TRAILER_TYPE);

                ContentValues trailerValues = new ContentValues();
                trailerValues.put(TrailerEntry.COLUMN_MOVIE_ID, movieRowId);
                trailerValues.put(TrailerEntry.COLUMN_NAME, name);
                trailerValues.put(TrailerEntry.COLUMN_SITE, site);
                trailerValues.put(TrailerEntry.COLUMN_TRAILER_KEY, key);
                trailerValues.put(TrailerEntry.COLUMN_SIZE, size);
                trailerValues.put(TrailerEntry.COLUMN_TYPE, type);

                cvVector.add(trailerValues);
            }

            //add all to database via bulkInsert call
            int insertCount = 0;
            if (cvVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                cvVector.toArray(cvArray);
                insertCount = mContext.getContentResolver().bulkInsert(TrailerEntry.CONTENT_URI, cvArray);
            }
            Log.d(LOG_TAG, "inserted: " + insertCount + " trailers in DB of movie(id): " + movieApiId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
