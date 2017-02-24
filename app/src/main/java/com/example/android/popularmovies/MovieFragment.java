package com.example.android.popularmovies;

import android.content.Intent;
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

public class MovieFragment extends Fragment implements OnTaskCompleted{

    private final String LOG_TAG = MovieFragment.class.getSimpleName();

    private ImageAdapter mMovieAdapter;
    private final String TOP_RATED = "top_rated";
    private final String MOST_POP = "popular";
    private String lastSelection;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                Log.i(LOG_TAG, "position:" + String.valueOf(position));
                Log.i(LOG_TAG, "movie clicked:" + mMovieAdapter.getItem(position));
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, mMovieAdapter.getItem(position).toString());
                startActivity(intent);
            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
        if (lastSelection == null) {
            updateMovies(MOST_POP);
            Toast.makeText(getActivity(), R.string.loading_most_popular,
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), R.string.loading_highest_rated,
                    Toast.LENGTH_LONG).show();
            updateMovies(lastSelection);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_highest_rated) {
            Log.i(LOG_TAG, "top rated was pressed");
            lastSelection = TOP_RATED;
            Toast.makeText(getActivity(), R.string.loading_highest_rated,
                    Toast.LENGTH_LONG).show();
            updateMovies(TOP_RATED);
            return true;
        } else if (id == R.id.action_sort_most_popular) {
            Log.i(LOG_TAG, "most pop was pressed");
            lastSelection = MOST_POP;
            Toast.makeText(getActivity(), R.string.loading_most_popular,
                    Toast.LENGTH_LONG).show();
            updateMovies(MOST_POP);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.e(LOG_TAG,"onSaveInstanceState");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.e(LOG_TAG,"onStop");
    }

    @Override
    public void onTaskCompleted(String[] result) {
        if (result != null) {
            mMovieAdapter.clear();
            Log.i(LOG_TAG, "Updating Adapter");
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


