package com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Benjamin Garza on 11/6/2016.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private final String LOG_TAG = ImageAdapter.class.getSimpleName();

    private List<String> movieInputs;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        Log.e(LOG_TAG, "getCount");
        if(movieInputs == null)
            return 10;
        return movieInputs.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e(LOG_TAG, "getView");
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(500, 750));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) convertView;
        }
        //todo need to set this to the new List created
        String defaultImageUri = "http://image.tmdb.org/t/p/w500//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg";
        Picasso.with(mContext).load(defaultImageUri).fit().into(imageView);
        //imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    public void clear(){
        if(movieInputs != null)
            movieInputs.clear();
    }

    public void add(int index, String input){
        if(movieInputs != null)
            movieInputs.add(index, input);
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.common_google_signin_btn_icon_dark,
            R.drawable.common_google_signin_btn_icon_dark,
            R.drawable.common_google_signin_btn_icon_dark,
            R.drawable.common_google_signin_btn_icon_dark
    };


}