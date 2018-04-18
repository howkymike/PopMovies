package com.example.mike.popmovies.detailactivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mike.popmovies.ImageUtils;
import com.example.mike.popmovies.LoadMoviesAdapter;
import com.example.mike.popmovies.MainActivity;
import com.example.mike.popmovies.MovieObject;
import com.example.mike.popmovies.Networking.NetworkUtils;
import com.example.mike.popmovies.R;
import com.example.mike.popmovies.data.MovieDbContract;
import com.example.mike.popmovies.data.MovieDbHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mike.popmovies.data.MovieContentProvider.TASK_WITH_ID;

//TODO handle AsyncTask when performing db operations

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    @BindView(R.id.tv_title) TextView mTitle;
    @BindView(R.id.tv_releaseDate) TextView mReleaseDate;
    @BindView(R.id.tv_overview) TextView mOverview;
    @BindView(R.id.image_backdrop) ImageView mBackdrop;
    @BindView(R.id.image_poster) ImageView mPoster;
    @BindView(R.id.rb_vote) RatingBar mRating;
    @BindView(R.id.rv_trailers) RecyclerView mRecyclerViewTrailers;
    @BindView(R.id.rv_reviews) RecyclerView mRecyclerViewReviews;
    @BindView(R.id.sv_detail) ScrollView mScrollView;

    private TrailersAdapter trailersAdapter;
    private List<JSONObject> trailersListJSON;

    private ReviewsAdapter reviewsAdapter;
    private List<JSONObject> reviewsListJSON;

    private MovieObject movie;
    private boolean fromNetwork;
    private int mFavourite = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent receivedIntent = getIntent();
        movie = (MovieObject) receivedIntent.getSerializableExtra(LoadMoviesAdapter.INTENT_MOVIE);

        fromNetwork = receivedIntent.getBooleanExtra(LoadMoviesAdapter.INTENT_FROMNETWORK, true);

        this.setTitle(movie.getTitle());


        mTitle.setText(movie.getTitle());

        String dateString = movie.getRelease_date();
        String date = "(" + dateString + ")";
        mReleaseDate.setText(date);

        mRating.setRating((float) movie.getVote_average() / 2);
        mOverview.setText(movie.getOverview());

        if (fromNetwork) {

            String backdropUrl = NetworkUtils.buildImageUrl(movie.getBackdrop_path());
            Picasso.with(this)
                    .load(backdropUrl)
                    .into(mBackdrop);
            String posterUrl = NetworkUtils.buildImageUrl(movie.getPoster_path());
            Picasso.with(this)
                    .load(posterUrl)
                    .into(mPoster);


            new TrailersTask(movie.getID()).execute();
        }
        else {
            String posterPath = String.valueOf(movie.getID()) + ".jpg";
            String backdropPath = String.valueOf(movie.getID()) + "b" + ".jpg";

            File backdropFile = new File(this.getFilesDir(), backdropPath);
            File posterFile = new File(this.getFilesDir(), posterPath);

            Picasso.with(this)
                    .load(backdropFile)
                    .into(mBackdrop);
            Picasso.with(this)
                    .load(posterFile)
                    .into(mPoster);

        }
    }


    public class TrailersTask extends AsyncTask<Void, Void, JSONObject[]> {

        private final int movieID;

        public TrailersTask(int movieID) {
            this.movieID = movieID;
        }

        @Override
        protected JSONObject[] doInBackground(Void... voids) {
            JSONObject jsonVideos = NetworkUtils.getMovieVideos(movieID);
            JSONObject jsonReviews = NetworkUtils.getMovieReviews(movieID);

            return new JSONObject[]{jsonVideos, jsonReviews};
        }


        @Override
        protected void onPostExecute(JSONObject[] jsonObjects) {
            createMovieViews(jsonObjects[0]);
            createReviewViews(jsonObjects[1]);
        }
    }


    public void createMovieViews(JSONObject jsonVideos) {
        try {
            JSONArray results = jsonVideos.getJSONArray("results");

            int numberOfTrailers = results.length();
            trailersListJSON = new ArrayList<>(numberOfTrailers);

            for (int i = 0; i < numberOfTrailers; i++) {
                JSONObject trailer = results.getJSONObject(i);
                trailersListJSON.add(i, trailer);
            }

            trailersAdapter = new TrailersAdapter(this);
            mRecyclerViewTrailers.setAdapter(trailersAdapter);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRecyclerViewTrailers.setLayoutManager(linearLayoutManager);

            mRecyclerViewTrailers.setHasFixedSize(true);

            trailersAdapter.setData(trailersListJSON);



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createReviewViews(JSONObject jsonReviews) {
        try {
            JSONArray results = jsonReviews.getJSONArray("results");
            int numberOfReviews = results.length();
            reviewsListJSON = new ArrayList<>(numberOfReviews);

            for (int i = 0; i < numberOfReviews; i++) {
                JSONObject review = results.getJSONObject(i);
                reviewsListJSON.add(i, review);
            }

            reviewsAdapter = new ReviewsAdapter(this);
            mRecyclerViewReviews.setAdapter(reviewsAdapter);

            LinearLayoutManager linearLayoutManagerReview = new LinearLayoutManager(this);
            mRecyclerViewReviews.setLayoutManager(linearLayoutManagerReview);
            mRecyclerViewReviews.setHasFixedSize(true);

            reviewsAdapter.setData(reviewsListJSON);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail_menu, menu);
        try {
            checkFavouriteFromDB(menu);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuitem_favourite:

                Drawable itemIcon = item.getIcon();

                if ( itemIcon.getConstantState().equals(getDrawable(R.drawable.ic_not_favourite).getConstantState()) ) {
                    item.setIcon(R.drawable.ic_favourite);
                    addMovieToDb(movie.getID(), movie.getTitle(), movie.getOverview(), movie.getVote_average(), movie.getRelease_date());
                    //Log.v(TAG, "newRowID: " + newRowId);


                    // saves images
                    BitmapDrawable draw = (BitmapDrawable) mPoster.getDrawable();
                    BitmapDrawable drawBackdrop = (BitmapDrawable) mBackdrop.getDrawable();

                    Bitmap bitmap = draw.getBitmap();
                    Bitmap bitmapBackdrop = drawBackdrop.getBitmap();

                    String fileName = String.valueOf(movie.getID()) + ".jpg";
                    String fileBackdrop = String.valueOf(movie.getID()) + "b" + ".jpg";

                    File outFile = new File(this.getFilesDir(), fileName);
                    File outFileBackdrop = new File(this.getFilesDir(), fileBackdrop);

                    FileOutputStream outStream;
                    FileOutputStream outStreamBackdrop;
                    try {
                        outStream = new FileOutputStream(outFile);
                        outStreamBackdrop = new FileOutputStream(outFileBackdrop);

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        bitmapBackdrop.compress(Bitmap.CompressFormat.JPEG, 100, outStreamBackdrop);

                        outStream.flush();
                        outStreamBackdrop.flush();

                        outStream.close();
                        outStreamBackdrop.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    item.setIcon(R.drawable.ic_not_favourite);
                    deleteMovieFromDb(movie.getID());
                    ImageUtils.deleteImage(this, String.valueOf(movie.getID()));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void checkFavouriteFromDB(Menu menu) throws InterruptedException {
        int favourite = 0;
        String movie_idString = String.valueOf(movie.getID());

        String[] projection = {
                MovieDbContract.MovieDbEntry._ID,
                MovieDbContract.MovieDbEntry.COLUMN_MOVIE_ID,
        };
        Cursor allDataCursor = getContentResolver().query(MovieDbContract.MovieDbEntry.CONTENT_URI, projection, null, null, MovieDbContract.MovieDbEntry._ID);
        while (allDataCursor.moveToNext()) {
            String temp_movieID = allDataCursor.getString(allDataCursor.getColumnIndex(MovieDbContract.MovieDbEntry.COLUMN_MOVIE_ID));
            Log.v(TAG, "movie_idString: " + movie_idString);
            Log.v(TAG, "temp_movieID: " + temp_movieID);

            if (temp_movieID.equals(movie_idString)) {
                mFavourite = 1;
            }
        }
        allDataCursor.close();



        MenuItem favouriteItem = menu.findItem(R.id.menuitem_favourite);
        if (mFavourite == 1) {
            favouriteItem.setIcon(R.drawable.ic_favourite);
        } else {
            favouriteItem.setIcon(R.drawable.ic_not_favourite);
        }
    }


    private void addMovieToDb(int movie_id, String title, String overview, double rating, String release_date) {

        ContentValues cv = new ContentValues();
        cv.put(MovieDbContract.MovieDbEntry.COLUMN_MOVIE_ID, movie_id);
        cv.put(MovieDbContract.MovieDbEntry.COLUMN_MOVIE_TITLE, title);
        cv.put(MovieDbContract.MovieDbEntry.COLUMN_MOVIE_OVERVIEW, overview);
        cv.put(MovieDbContract.MovieDbEntry.COLUMN_MOVIE_RATING, rating);
        cv.put(MovieDbContract.MovieDbEntry.COLUMN_MOVIE_RELEASE_DATE, release_date);


        Uri uri = getContentResolver().insert(MovieDbContract.MovieDbEntry.CONTENT_URI, cv);
        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void deleteMovieFromDb(long id) {
        Uri uri = MovieDbContract.MovieDbEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(id)).build();
        getContentResolver().delete(uri, MovieDbContract.MovieDbEntry.COLUMN_MOVIE_ID + "=" + id, null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("ARTICLE_SCROLL_POSITION",
                new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
        if(position != null)
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);
                }
            });
    }
}

