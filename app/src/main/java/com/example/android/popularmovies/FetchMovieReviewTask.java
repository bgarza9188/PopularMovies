package com.example.android.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jebus on 2/23/2017.
 */

public class FetchMovieReviewTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchMovieReviewTask.class.getSimpleName();

    public OnTaskCompleted listener = null;

    /**
     * Take the String representing the complete Review list in JSON Format and
     * pull out the data we need.
     *
     * The constructor takes the JSON string and converts it into an Object hierarchy.
     */
    private String[] getReviewDataFromJson(String movieJsonStr)
            throws JSONException {

        String[] resultStrings;
        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";

        JSONObject reviewJson = new JSONObject(movieJsonStr);
        JSONArray reviewArray = reviewJson.getJSONArray(RESULTS);
        resultStrings = new String[reviewArray.length()];

        for(int i = 0; i < reviewArray.length(); i++) {
            JSONObject review = reviewArray.getJSONObject(i);
            resultStrings[i] = review.toString();
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
        //Will contain reviews
        String movieReviewJsonStr = null;

        try {
            // Construct the URL for the MovieDB query
            // The URL will look like this https://api.themoviedb.org/3/movie/{movie_ID}/reviews?api_key=***&language=en-US&page=1
            final String POPULAR_MOVIE_BASE_URL =
                    "http://api.themoviedb.org/3/movie/";
            final String APPID_PARAM = "api_key";

            String movie_ID = params[0];

            Uri builtUri = Uri.parse(POPULAR_MOVIE_BASE_URL).buildUpon()
                    .appendPath(movie_ID)
                    .appendPath("reviews")
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
            movieReviewJsonStr = buffer.toString();
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
            Log.i(LOG_TAG, movieReviewJsonStr);
            return getReviewDataFromJson(movieReviewJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
        listener.onTaskCompleted(result);
    }
}
