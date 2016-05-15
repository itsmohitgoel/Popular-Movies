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
 * Created by Mohit on 15-05-2016.
 */
public class FetchMoviesAsync extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMoviesAsync.class.getSimpleName();
    private ArrayList<MovieItem> mDataList;
    public IAsyncListener mListener;

    public FetchMoviesAsync(ArrayList<MovieItem> mList) {
        this.mDataList = mList;
    }
    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            //Make url and open connection
            Uri baseURI = Uri.parse(params[0]);
            URL url = new URL(baseURI.toString());

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
            StringBuffer bufferString = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                bufferString.append(line).append("\n");
            }

            if (bufferString.length() == 0) {
                return  null;
            }
            String moviesJsonString = bufferString.toString();
            parseJson(moviesJsonString);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error in downloading");
        }finally {
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
        mListener.onAsyncEnd(mDataList);
    }

    private void parseJson(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("posts");
            MovieItem item;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("title");
                item = new MovieItem();
                JSONArray attachments = post.getJSONArray("attachments");
                if (null != attachments && attachments.length() > 0) {
                    JSONObject attachment = attachments.getJSONObject(0);
                    if (attachment != null)
                        item.setPosterURL(attachment.getString("url"));
                }
                mDataList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
