package com.example.android.popularmovies.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.fragments.MovieFragment;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Log.i(LOG_TAG,"onCreate");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_main, new MovieFragment())
                    .commit();
            Toast.makeText(this, R.string.toast_onStart_guide,
                    Toast.LENGTH_LONG).show();
        }
    }
}