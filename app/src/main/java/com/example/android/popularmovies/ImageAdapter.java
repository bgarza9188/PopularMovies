package com.example.android.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin Garza on 11/6/2016.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private final String LOG_TAG = ImageAdapter.class.getSimpleName();

    protected List<String> movieInputs;

    public ImageAdapter(Context c) {
        mContext = c;
        movieInputs = new ArrayList<>();
    }
    public int getCount() {
        if(movieInputs == null)
            return 0;
        return movieInputs.size();
    }

    public Object getItem(int position) {
        return movieInputs.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) convertView;
        }

        String defaultImageUri = "http://image.tmdb.org/t/p/w500";
        defaultImageUri += getMoviePosterURL(movieInputs.get(position));
        Picasso.with(mContext).load(defaultImageUri).into(imageView);
        return imageView;
    }

    protected String getMoviePosterURL(String movie) {
        try {
            JSONObject movieJson = new JSONObject(movie);
            String poster = movieJson.getString("poster_path");
            return poster;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clear(){
        if(movieInputs != null)
            movieInputs.clear();
    }

    public void add(int index, String input){
        if(movieInputs != null)
            movieInputs.add(index, input);
    }
}