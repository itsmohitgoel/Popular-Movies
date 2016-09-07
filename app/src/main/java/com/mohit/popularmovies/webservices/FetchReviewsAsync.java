package com.mohit.popularmovies.webservices;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.mohit.popularmovies.beans.ReviewItem;
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
import java.util.ArrayList;

/**
 * Created by Mohit on 02-09-2016.
 */
public class FetchReviewsAsync extends AsyncTask<String, Void, Void> {
    public static final String LOG_TAG = FetchReviewsAsync.class.getSimpleName();

    public IAsyncListener mReviewsCallback; //callback object(or listener) to be notified of completion
    private ArrayList<ReviewItem> mReviewItems;

    public FetchReviewsAsync(IAsyncListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("IAsyncListener cannot be null");
        }
        mReviewsCallback = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mReviewsCallback.onAsyncBegin();
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //Throw exception on missing api key
        if (PopConstants.API_KEY.equals("ADD Your API KEY HERE")) {
            throw new IllegalArgumentException("Please Enter your api key in PopConstants Class !");
        }
        String movieId = params[0];
        if (movieId == null || movieId.equals("")) {
            return null;
        }
        try {
            // Make url and open connection
            Uri baseURI = Uri.parse(PopConstants.BASE_URL);
            Uri.Builder builder = baseURI.buildUpon()
                    .appendPath(movieId)
                    .appendPath(PopConstants.REVIEW_PATH)
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

            String reviewJsonString = bufferString.toString();
            parseJson(reviewJsonString);
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

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mReviewsCallback.onAsyncStop(mReviewItems);
    }

    private void parseJson(String reviewJsonString) {
        try {
            JSONObject jsonObject = new JSONObject(reviewJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(PopConstants.RESULTS);

            mReviewItems = new ArrayList<ReviewItem>(jsonArray.length());
            ReviewItem reviewItem;

            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject reviewJson = jsonArray.getJSONObject(i);

                reviewItem = new ReviewItem();
                String reviewID = reviewJson.getString(PopConstants.REVIEW_ID);
                String author = reviewJson.getString(PopConstants.REVIEW_AUTHOR);
                String content = reviewJson.getString(PopConstants.REVIEW_CONTENT);

                reviewItem.setAuthour(author);
                reviewItem.setContent(content);
                reviewItem.setReviewId(reviewID);

                mReviewItems.add(reviewItem);
            }

        } catch (JSONException je) {
            je.printStackTrace();
            Log.e(LOG_TAG, "Error in parsing Review Item");
        }
    }
}
