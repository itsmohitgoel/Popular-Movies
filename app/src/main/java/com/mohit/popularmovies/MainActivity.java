package com.mohit.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intentSettings = new Intent(getApplication(), SettingsActivity.class);
            startActivity(intentSettings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Get Preference value for Activity
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = mPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            if (sortOrder.equalsIgnoreCase(getString(R.string.pref_sort_by_popularity_value))) {
                actionBar.setTitle(String.format("%s Movies",getString(R.string.pref_sort_by_popularity_label)));
            }else {
                actionBar.setTitle(String.format("%s Movies",getString(R.string.pref_sort_by_rating_label)));
            }
        }
    }
}
