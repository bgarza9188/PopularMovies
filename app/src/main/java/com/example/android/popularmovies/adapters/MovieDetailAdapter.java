package com.example.android.popularmovies.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin Garza on 11/6/2016.
 */

public class MovieDetailAdapter extends BaseAdapter {
    private Context mContext;
    private final String LOG_TAG = MovieDetailAdapter.class.getSimpleName();
    private String title;
    private String releaseDate;
    private String voteAverage;
    private String plot;
    private String movie_id;
    private String mMovieStr;
    private Boolean favoriteFlag;

    public static class ViewHolder {
        public ImageView imageView;
        public TextView plotView;
        public TextView voteAverageView;
        public TextView releaseDateView;
        public TextView movieTitleView;
        public TextView listItemView;
        public Button favoriteButton;

        public ViewHolder(View view) {
            movieTitleView = (TextView) view.findViewById(R.id.movie_title);
            releaseDateView = (TextView) view.findViewById(R.id.release_date);
            voteAverageView = (TextView) view.findViewById(R.id.vote_average);
            plotView = (TextView) view.findViewById(R.id.plot_synopsis);
            imageView = (ImageView) view.findViewById(R.id.detail_image_view);
            favoriteButton = (Button) view.findViewById(R.id.favorite_button);
            listItemView = (TextView) view.findViewById(R.id.list_item_textView);
        }
    }

    protected List<String> movieInputs;

    public MovieDetailAdapter(Context c) {
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
        View view;
        final ViewHolder viewHolder;
        int layoutId;
        
        if (position == 0) {
            layoutId = R.layout.list_item_movie_detail;
        }  else {
            layoutId = R.layout.list_item;
        }
        view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        viewHolder = new ViewHolder(view);
                
        if(position == 0){
            viewHolder.movieTitleView.setText(title);
            viewHolder.releaseDateView.setText(releaseDate);
            viewHolder.voteAverageView.setText(String.format(mContext.getResources().getString(R.string.out_of_10_rating), voteAverage));
            viewHolder.plotView.setText(plot);
            ImageAdapter imageAdapter = new ImageAdapter(mContext);
            String moviePosterURL = ImageAdapter.BASE_POSTER_IMAGE_URL + imageAdapter.getMoviePosterURL(mMovieStr);
            Picasso.with(mContext).load(moviePosterURL).into(viewHolder.imageView);
            if(favoriteFlag){
                viewHolder.favoriteButton.setText(R.string.unmark_favorite_button);
            } else {
                viewHolder.favoriteButton.setText(R.string.mark_favorite_button);
            }
            viewHolder.favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(favoriteFlag) {
                        favoriteFlag = false;
                        viewHolder.favoriteButton.setText(R.string.mark_favorite_button);
                        mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, "movie_id = " + movie_id, null);
                    } else {
                        favoriteFlag = true;
                        viewHolder.favoriteButton.setText(R.string.unmark_favorite_button);
                        Toast.makeText(mContext, R.string.marked_as_favorite,
                                Toast.LENGTH_SHORT).show();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie_id);
                        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_STRING, mMovieStr);
                        mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,contentValues);
                    }
                }
            });
        } else {
            if(movieInputs.get(position).contains("trailer")) {
                String[] keyArray;
                keyArray = movieInputs.get(position).split(",");
                viewHolder.listItemView.setText(keyArray[3]);
            } else {
                viewHolder.listItemView.setText(movieInputs.get(position));
            }
        }
        return view;
    }

    public void clear(){
        if(movieInputs != null)
            movieInputs.clear();
    }

    public void add(int index, String input, Boolean mIsFavorite){
        if(movieInputs != null) {
            if (index == 0) {
                getMovieInfoFromIntent(input);
                favoriteFlag = mIsFavorite;
            }
            movieInputs.add(index, input);
        }
    }

    public void getMovieInfoFromIntent(String mMovieStr) {
        this.mMovieStr = mMovieStr;
        try {
            JSONObject movieJson = new JSONObject(mMovieStr);
            title = movieJson.getString("title");
            releaseDate = movieJson.getString("release_date");
            voteAverage = movieJson.getString("vote_average");
            plot = movieJson.getString("overview");
            movie_id = movieJson.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}