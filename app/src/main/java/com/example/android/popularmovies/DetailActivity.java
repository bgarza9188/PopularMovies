package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_detail, new DetailFragment())
                    .commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment  implements OnTaskCompleted{

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private String mMovieStr;
        private String movieDetailString;
        private String title;
        private String releaseDate;
        private String voteAverage;
        private String plot;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // The detail Activity called via intent.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                mMovieStr = intent.getStringExtra(Intent.EXTRA_TEXT);
                try {
                    JSONObject movieJson = new JSONObject(mMovieStr);
                    title = movieJson.getString("title");
                    releaseDate = movieJson.getString("release_date");
                    voteAverage = movieJson.getString("vote_average");
                    plot = movieJson.getString("overview");
                    getReviews(movieJson.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                movieDetailString = "TITLE: \n" + title + "\n\n" +
                                    "RELEASE DATE: \n" + releaseDate + "\n\n" +
                                    "VOTE AVERAGE: \n" + voteAverage + "\n\n" +
                                    "PLOT SYNOPSIS: \n" + plot + "\n\n";
                ((TextView) rootView.findViewById(R.id.detail_text))
                        .setText(movieDetailString);
                String defaultImageUri = "http://image.tmdb.org/t/p/w500";
                defaultImageUri += new ImageAdapter(getActivity()).getMoviePosterURL(mMovieStr);
                Log.e(LOG_TAG, defaultImageUri);
                ImageView imageView = ((ImageView) rootView.findViewById(R.id.detail_image_view));
                Picasso.with(getContext()).load(defaultImageUri).into(imageView);
            }
            return rootView;
        }

        private void getReviews(String param) {
            FetchMovieReviewTask task = new FetchMovieReviewTask();
            task.listener=this;
            task.execute(param);
        }

        @Override
        public void onTaskCompleted(String[] result) {
        }
    }
}
