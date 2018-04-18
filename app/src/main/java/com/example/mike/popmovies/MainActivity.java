package com.example.mike.popmovies;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mike.popmovies.Networking.NetworkUtils;
import com.example.mike.popmovies.data.MovieDbContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    // TODO change hard-coded resize value (should depend on screen size)
    private static final int numberOfColumnsPortrait = 4;
    private static final int numberOfColumnsLandscape = 5;
    private static final String ON_SAVED_TYPE_RECORD_KEY = "typeOfRecord";
    private static final String MY_PREFS = "myPrefernces";

    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.recyclerview_main) RecyclerView mRecyclerView;
    @BindView(R.id.frameLayout_main) FrameLayout mLayoutManager;

    private LoadMoviesAdapter loadMoviesAdapter;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private ArrayList<MovieObject> movies;
    private int mPage;
    private String mTypeOfRecord;
    public boolean isOnline;
    private LinearLayoutManager mGridLayoutManager;
    Parcelable mListState;

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int TASK_LOADER_ID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        // checks if Online
        SharedPreferences prefs = getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        String restoredTypeOfRecord = prefs.getString(ON_SAVED_TYPE_RECORD_KEY, null);
        if (restoredTypeOfRecord != null) {
            mTypeOfRecord = restoredTypeOfRecord;
        } else {
            mTypeOfRecord = "popular";
        }

        isOnline = NetworkUtils.isOnline(this);
        if (!isOnline) {

            final AlertDialog.Builder localOnlyDialogBuilder = new AlertDialog.Builder(this);
            localOnlyDialogBuilder.setTitle(R.string.no_internet_connection)
                    .setMessage(R.string.favourites_only)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mTypeOfRecord = "favourites";
                        }
                    });

            final AlertDialog.Builder alterDialogBuilder = new AlertDialog.Builder(this);
            alterDialogBuilder.setTitle(R.string.no_internet_connection)
                    .setMessage(R.string.no_internet_message)
                    .setPositiveButton(R.string.no_internet_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //finish();
                                    localOnlyDialogBuilder.show();
                                }
                            }
                    );

            alterDialogBuilder.show();
        }


            movies = new ArrayList<MovieObject>();

        mPage = 1;


            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mGridLayoutManager = new GridLayoutManager(this, numberOfColumnsPortrait);
            } else {
                mGridLayoutManager = new GridLayoutManager(this, numberOfColumnsLandscape);
            }
            mRecyclerView.setLayoutManager(mGridLayoutManager);

            loadMoviesAdapter = new LoadMoviesAdapter(this);
            mRecyclerView.setAdapter(loadMoviesAdapter);

            mScrollListener = new EndlessRecyclerViewScrollListener(mGridLayoutManager) {

                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    mPage++;
                    Log.v(MainActivity.class.getSimpleName(), "onLoadMore:  " + mPage + " " + mTypeOfRecord);

                    if (isOnline && !mTypeOfRecord.equals("favourites")) {
                        new TheMovieRequestTask().execute();
                    } else {
                        // do sth with cursor
                    }
                }
            };

            mRecyclerView.addOnScrollListener(mScrollListener);

            mRecyclerView.setHasFixedSize(true);

            mRecyclerView.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);


        if (isOnline && !mTypeOfRecord.equals("favourites")) {
            new TheMovieRequestTask().execute();
        }
        else {
            getMainDataFromDb();
            mRecyclerView.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }

}



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "onCreateLoader");
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mMovieData;

            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MovieDbContract.MovieDbEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MovieDbContract.MovieDbEntry._ID + " ASC");
                } catch (Exception e) {
                    noData();
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                if (data.getCount() == 0) {
                    noData();
                }
                mMovieData = data;
                loadMoviesAdapter.setMovieData(mMovieData);
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "onFinished");
        if (data == null) {
            noData();
        }
        loadMoviesAdapter.setMovieData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.v(TAG, "onLoaderRestart");

    }

//TODO improve rotation (porbably need to override onConfigurationChanged()

    public class TheMovieRequestTask extends AsyncTask<Void, Void, JSONObject> {


        @Override
        protected JSONObject doInBackground(Void... voids) {

            Log.v(TAG, "page in doInBackground:" + mPage);
            URL searchUrl = NetworkUtils.buildUrl(mTypeOfRecord, mPage);

            String theMovieSearchResults;
            JSONObject jsonObject = null;
            try {
                theMovieSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                try {
                    jsonObject = new JSONObject(theMovieSearchResults);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject theMovieSearchResults) {

            if (theMovieSearchResults != null && !theMovieSearchResults.equals("")) {

                ArrayList<MovieObject> moreMovies = JsonUtils.parseJson2Movies(theMovieSearchResults);
                movies.addAll(moreMovies);


                loadMoviesAdapter.setMovieData(movies);
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mErrorMessageDisplay.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                noData();
            }



        }
    }



    private void noData() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.sortBy)
                        .setItems(R.array.sort_options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                movies.clear();
                                mPage = 1;
                                mScrollListener.resetState();
                                switch (which) {
                                    case 0:
                                        mTypeOfRecord = "popular";
                                        break;
                                    case 1:
                                        mTypeOfRecord = "top_rated";
                                        break;
                                    case 2:
                                        mTypeOfRecord = "upcoming";
                                        break;
                                    case 3:
                                        mTypeOfRecord = "favourites";
                                        getMainDataFromDb();

                                        break;
                                    default:
                                        mTypeOfRecord = "popular";

                                }

                                if (!mTypeOfRecord.equals("favourites")) {
                                    mRecyclerView.setVisibility(View.INVISIBLE);
                                    mLoadingIndicator.setVisibility(View.VISIBLE);
                                    new TheMovieRequestTask().execute();
                                } else {
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                builder.show();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void getMainDataFromDb() {
        Log.v(TAG, "getting data from loader!");
        getSupportLoaderManager().destroyLoader(TASK_LOADER_ID);
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS, MODE_PRIVATE).edit();
        editor.putString(ON_SAVED_TYPE_RECORD_KEY, mTypeOfRecord);
        editor.apply();


        Log.v(TAG, "onSaveInstanceState is called");
        outState.putInt("mPageKey", mPage);
        mListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("mListStateKey", mListState);

        outState.putInt("preItemCount", mScrollListener.getPrevItemCount());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.v(TAG, "mTypeOfRecord in OnResume: " + mTypeOfRecord);
        if (mTypeOfRecord.equals("favourites")) {
            getMainDataFromDb();
        }
    }
}

