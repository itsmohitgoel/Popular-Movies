package com.mohit.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
}
