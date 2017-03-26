package com.example.android.popularmovies.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.activities.DetailActivity;
import com.example.android.popularmovies.adapters.FavoriteMovieAdapter;
import com.example.android.popularmovies.data.MovieContract;

/**
 * A fragment representing a list of Favorite Movies.
 *
 */
public class FavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = FavoriteFragment.class.getSimpleName();

    private static final int FAVORITE_LOADER = 0;


    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_STRING,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID
    };

    public static final int COL_MOVIE_ENTRY_ID = 0;
    public static final int COL_MOVIE_STRING= 1;
    public static final int COL_MOVIE_TMDB_ID = 2;

    private FavoriteMovieAdapter mCursorAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FavoriteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favorite_movie_list, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.favorite_movie_grid_view);

        mCursorAdapter = new FavoriteMovieAdapter(getActivity(), null, 0);

        gridView.setAdapter(mCursorAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if(getActivity().getClass().getName().contains("DetailActivity")){
                    getActivity().finish();
                }
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, mCursorAdapter.getCursor().getString(COL_MOVIE_STRING))
                        .putExtra("favorite_flag", "true");
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onActivityCreated");
        getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "onCreateLoader");
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(LOG_TAG, "onLoadFinished");
        if(cursor.getCount() == 0){
            Toast.makeText(getActivity(), R.string.no_favorites,
                    Toast.LENGTH_LONG).show();
        }
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
