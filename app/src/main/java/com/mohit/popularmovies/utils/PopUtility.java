package com.mohit.popularmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.mohit.popularmovies.R;

/**
 * Class containing other useful methods and variables
 * Created by Mohit on 16-05-2016.
 */
public class PopUtility {
    /**
     * To check the network status i.e connected or not
     * @param context
     * @return boolean
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected()) ? true : false;
    }

    public static String getSortingPreferrence(Context context) {
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        String sortPreference = sharedPreference.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));

        return sortPreference;
    }
}
