package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieFragment extends Fragment {

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


        mMovieAdapter = new ImageAdapter(getActivity());

        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        // Get a reference to the GridView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid_view);
        gridView.setAdapter(mMovieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
//                Toast.makeText(getActivity(), position,
//                        Toast.LENGTH_SHORT).show();
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
        Log.i(LOG_TAG,"onStart");
        if(lastSelection == null) {
            updateMovies(MOST_POP);
            Toast.makeText(getActivity(), R.string.loading_most_popular,
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getActivity(), R.string.loading_highest_rated,
                    Toast.LENGTH_LONG).show();
            updateMovies(lastSelection);
        }

    }

    private void updateMovies(String param) {
        FetchPopularMovieTask task = new FetchPopularMovieTask();
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
        }
        else if (id == R.id.action_sort_most_popular) {
            Log.i(LOG_TAG, "most pop was pressed");
            lastSelection = MOST_POP;
            Toast.makeText(getActivity(), R.string.loading_most_popular,
                    Toast.LENGTH_LONG).show();
            updateMovies(MOST_POP);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchPopularMovieTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchPopularMovieTask.class.getSimpleName();

        /**
         * Take the String representing the complete Movie list in JSON Format and
         * pull out the data we need.
         *
         * The constructor takes the JSON string and converts it into an Object hierarchy.
         */
        private String[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            String[] resultStrings;
            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "results";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);
            resultStrings = new String[movieArray.length()];

            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                resultStrings[i] = movie.toString();
            }

            return resultStrings;
        }

        @Override
        protected String[] doInBackground(String... params) {

            if(params.length == 0)
                return null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuffer buffer;
            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
            try {
                // Construct the URL for the MovieDB query
                final String POPULAR_MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/movie/";
                final String APPID_PARAM = "api_key";

                String category_path = params[0];

                Uri builtUri = Uri.parse(POPULAR_MOVIE_BASE_URL).buildUpon()
                        .appendPath(category_path)
                        .appendQueryParameter(APPID_PARAM, BuildConfig.POPULAR_MOVIE_API_KEY)
                        .build();
                Log.i("built URI", builtUri.toString());
                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    //Put newline at end of each line in JSON results
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    Log.i(LOG_TAG, "Stream was empty, returning null");
                    return null;
                }
                movieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("error", "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("error", "Error closing stream", e);
                    }
                }
            }

            try {
                Log.i(LOG_TAG, movieJsonStr);
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mMovieAdapter.clear();
                Log.i(LOG_TAG, "Updating Adapter");
                for(int i = 0; i < result.length; i++){
                    mMovieAdapter.add(i, result[i]);
                }
                mMovieAdapter.notifyDataSetChanged();
                // New data is back from the server.  Hooray!
            }
        }
    }
}
