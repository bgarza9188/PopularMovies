package com.example.android.popularmovies.fragments;

import android.content.Intent;
import android.database.Cursor;
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
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popularmovies.OnTaskCompleted;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.activities.DetailActivity;
import com.example.android.popularmovies.adapters.ImageAdapter;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.tasks.FetchPopularMovieTask;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieFragment extends Fragment implements OnTaskCompleted {

    private static final String VIEW_STATE_KEY = "view_state_key";
    private final String LOG_TAG = MovieFragment.class.getSimpleName();

    private ImageAdapter mMovieAdapter;
    private final String TOP_RATED = "top_rated";
    private final String MOST_POP = "popular";
    public static final String FAVORITE_TAG = "favorite";
    private String mLastSelection = "";

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mLastSelection = savedInstanceState.getString(VIEW_STATE_KEY);
        }

        setHasOptionsMenu(true);
        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        Log.i(LOG_TAG, "onCreateOptionsMenu");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        // Get a reference to the GridView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid_view);
        mMovieAdapter = new ImageAdapter(getActivity());

        gridView.setAdapter(mMovieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String value = "false";
                if(getFavoriteMovie(mMovieAdapter.getItem(position).toString())) {
                    value = "true";
                }
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, mMovieAdapter.getItem(position).toString())
                        .putExtra("favorite_flag", value);
                startActivity(intent);
            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
        if (mLastSelection.equalsIgnoreCase("") || mLastSelection.equalsIgnoreCase(MOST_POP)) {
            //When the app first launches, mLastSelection will be null.
            Toast.makeText(getActivity(), R.string.loading_most_popular,
                    Toast.LENGTH_SHORT).show();
            updateMovies(MOST_POP);
        } else if (mLastSelection.equals(FAVORITE_TAG)) {
            Toast.makeText(getActivity(), R.string.loading_favorites,
                    Toast.LENGTH_SHORT).show();
        } else if (mLastSelection.equals(TOP_RATED)){
            Toast.makeText(getActivity(), R.string.loading_highest_rated,
                    Toast.LENGTH_SHORT).show() ;
            updateMovies(TOP_RATED);
        }
    }

    private void updateMovies(String param) {
        FetchPopularMovieTask task = new FetchPopularMovieTask();
        task.listener=this;
        task.execute(param);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_sort_highest_rated) {
            mLastSelection = TOP_RATED;
            Toast.makeText(getActivity(), R.string.loading_highest_rated,
                    Toast.LENGTH_SHORT).show();
            //remove favorite fragment if it's there
            if(getActivity().getSupportFragmentManager().findFragmentByTag(FAVORITE_TAG) != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(getActivity().getSupportFragmentManager().findFragmentByTag(FAVORITE_TAG))
                        .commit();
            }
            updateMovies(TOP_RATED);
            return true;
        } else if (id == R.id.action_sort_most_popular) {
            mLastSelection = MOST_POP;
            Toast.makeText(getActivity(), R.string.loading_most_popular,
                    Toast.LENGTH_SHORT).show();
            //remove favorite fragment if it's there
            if(getActivity().getSupportFragmentManager().findFragmentByTag(FAVORITE_TAG) != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(getActivity().getSupportFragmentManager().findFragmentByTag(FAVORITE_TAG))
                        .commit();
            }
            updateMovies(MOST_POP);
            return true;

        } else if (id == R.id.action_show_favorites) {
            mLastSelection = FAVORITE_TAG;
            Toast.makeText(getActivity(), R.string.loading_favorites,
                    Toast.LENGTH_SHORT).show();
            if(getActivity().getSupportFragmentManager().findFragmentByTag(FAVORITE_TAG) == null){
                FavoriteFragment favoriteFragment = new FavoriteFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.content_main, favoriteFragment, FAVORITE_TAG)
                        .commit();
            }
            mMovieAdapter.clear();
            mMovieAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        Log.i(LOG_TAG,"onSaveInstanceState");
        savedInstanceState.putString(VIEW_STATE_KEY, mLastSelection);
        super.onSaveInstanceState(savedInstanceState);
    }

    public Boolean getFavoriteMovie(String movieStringToCheck) {
        String[] projection = {MovieContract.MovieEntry.COLUMN_MOVIE_ID};
        String movie_id = "";

        try {
            JSONObject movieJson = new JSONObject(movieStringToCheck);
            movie_id = "movie_id =" + movieJson.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Cursor cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                projection,
                movie_id,
                null,
                null
        );

        if(cursor != null && cursor.getCount() > 0){
            cursor.close();
            return true;
        } else if(cursor != null){
            cursor.close();
        }
        return false;
    }

    @Override
    public void onTaskCompleted(String[] result) {
        if (result != null) {
            mMovieAdapter.clear();
            for(int i = 0; i < result.length; i++){
                mMovieAdapter.add(i, result[i]);
            }
            mMovieAdapter.notifyDataSetChanged();
            // New data is back from the server.  Hooray!
        }
        else {
            Toast.makeText(getActivity(), R.string.result_is_null,
                    Toast.LENGTH_SHORT).show();
        }
    }
}


