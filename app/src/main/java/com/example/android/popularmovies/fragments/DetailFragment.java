package com.example.android.popularmovies.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.popularmovies.OnTaskCompleted;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapters.MovieDetailAdapter;
import com.example.android.popularmovies.tasks.FetchMovieReviewTask;
import com.example.android.popularmovies.tasks.FetchMovieVideoTask;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This fragment builds the movie details screen.
 */
public class DetailFragment extends Fragment implements OnTaskCompleted {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private String mMovieStr;
    private MovieDetailAdapter mMovieDetailAdapter;
    private Intent intent;
    private String mMovieID;
    private Boolean mIsFavorite = false;
    public static final String DETAIL_TAG = "detail";


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_favorites, menu);
        Log.i(LOG_TAG, "onCreateOptionsMenu");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.i(LOG_TAG, "onOptionsItemSelected");
        int id = item.getItemId();
        if (id == R.id.action_show_favorites) {
            Toast.makeText(getActivity(), R.string.loading_favorites,
                    Toast.LENGTH_SHORT).show();
            if(getActivity().getSupportFragmentManager().findFragmentByTag(MovieFragment.FAVORITE_TAG) == null){
                FavoriteFragment favoriteFragment = new FavoriteFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.content_main, favoriteFragment, MovieFragment.FAVORITE_TAG)
                        .commit();
            }
            mMovieDetailAdapter.clear();
            mMovieDetailAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_detail_main, container, false);

        ListView movieDetailListView = (ListView) rootView.findViewById(R.id.listview_movie_detail);
        mMovieDetailAdapter = new MovieDetailAdapter(getActivity());
        movieDetailListView.setAdapter(mMovieDetailAdapter);

        movieDetailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String key = mMovieDetailAdapter.getItem(position).toString();
                String[] keyArray;
                //starts up youtube for given trailer click.
                if(key.contains("trailer")){
                    keyArray = key.split(",");
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + keyArray[1])));
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart(){
        Log.i(LOG_TAG, "onStart");
        super.onStart();
        if(getActivity().getSupportFragmentManager().findFragmentByTag(MovieFragment.FAVORITE_TAG) != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .remove(getActivity().getSupportFragmentManager().findFragmentByTag(MovieFragment.FAVORITE_TAG))
                    .commit();
        }
        // The detail Activity called via intent.
        intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)
                && intent.hasExtra("favorite_flag")) {
            mMovieStr = intent.getStringExtra(Intent.EXTRA_TEXT);
            if(intent.getStringExtra("favorite_flag").equalsIgnoreCase("true")) {
                mIsFavorite = true;
            }
            try {
                JSONObject movieJson = new JSONObject(mMovieStr);
                mMovieID = movieJson.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mMovieDetailAdapter.clear();
        mMovieDetailAdapter.add(0, mMovieStr, mIsFavorite);
        getVideos(mMovieID);
        getReviews(mMovieID);
    }

    private void getVideos(String id) {
        FetchMovieVideoTask task = new FetchMovieVideoTask();
        task.listener=this;
        task.execute(id);
    }

    private void getReviews(String id) {
        FetchMovieReviewTask task = new FetchMovieReviewTask();
        task.listener=this;
        task.execute(id);
    }

    @Override
    public void onTaskCompleted(String[] result) {
        Log.i(LOG_TAG, "onTaskCompleted");
        if(result != null) {
            if (result[0].equalsIgnoreCase("review")) {
                //Pull Reviews
                if (result.length > 1) {
                    int position = mMovieDetailAdapter.getCount();
                    for (int i = 1; i < result.length; i++) {
                        JSONObject reviewJson;
                        String author = "";
                        String reviewText = "";
                        try{
                            reviewJson = new JSONObject(result[i]);
                            author = reviewJson.getString("author");
                            reviewText = reviewJson.getString("content");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mMovieDetailAdapter.add(position, "Author: " + author +  "\n" + "Review:\n" + reviewText, false);
                        position++;
                    }
                    mMovieDetailAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), R.string.no_reviews,
                            Toast.LENGTH_SHORT).show();
                }
            } else if (result[0].equalsIgnoreCase("video")) {
                //Pull Trailers first
                if (result.length > 1) {
                    for (int i = 1; i < result.length; i++) {
                        JSONObject videoJson;
                        String key = "";
                        String trailerName = "";
                        try {
                            videoJson = new JSONObject(result[i]);
                            key = videoJson.getString("key");
                            trailerName = videoJson.getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mMovieDetailAdapter.add(i, "trailer," + key + ",name," + trailerName, false);
                        mMovieDetailAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.no_videos,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(getActivity(), R.string.result_is_null,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
