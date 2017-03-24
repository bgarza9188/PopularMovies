package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.fragments.FavoriteFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by jebus on 3/20/2017.
 */

public class FavoriteMovieAdapter extends CursorAdapter {
    public FavoriteMovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        return imageView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String movieString = cursor.getString(FavoriteFragment.COL_MOVIE_STRING);
        ImageAdapter imageAdapter = new ImageAdapter(context);
        String moviePosterURL = imageAdapter.BASE_POSTER_IMAGE_URL + imageAdapter.getMoviePosterURL(movieString);
        Picasso.with(context).load(moviePosterURL).into((ImageView) view);
    }
}
