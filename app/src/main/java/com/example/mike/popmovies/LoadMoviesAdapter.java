package com.example.mike.popmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;

import com.example.mike.popmovies.Networking.NetworkUtils;
import com.example.mike.popmovies.data.MovieDbContract;
import com.example.mike.popmovies.detailactivity.DetailActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Mike on 2/16/2018.
 */

public class LoadMoviesAdapter extends RecyclerView.Adapter<LoadMoviesAdapter.LoadMoviesAdapterViewHolder> {

    public static final String INTENT_MOVIE = "movie_s";
    public static final String INTENT_FROMNETWORK = "from_network";

    private final Context mContext;
    private final LayoutInflater mInflater;
    private ArrayList<MovieObject> mData;
    private Cursor mCursor;
    private boolean fromNetwork;


    public LoadMoviesAdapter(Context context) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public LoadMoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new LoadMoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LoadMoviesAdapterViewHolder holder, int position) {
        if (fromNetwork) {
            String image_path = mData.get(position).getPoster_path();
            String imageURL = NetworkUtils.buildImageUrl(image_path);

            Picasso.with(mContext)
                    .load(imageURL)
                    .resize(300, 500)
                    .into(holder.imageView);
        }
        else {
            if (!mCursor.moveToPosition(position)) {
                return;
            }

            int image_id = mCursor.getInt(mCursor.getColumnIndex(MovieDbContract.MovieDbEntry.COLUMN_MOVIE_ID)) ;
            String image_path = image_id + ".jpg";

            // TODO change hard-coded resize value (should depend on screen size)
            File file = new File(mContext.getFilesDir(), image_path);
            Picasso.with(mContext)
                    .load(file)
                    .resize(300, 500)
                    .into(holder.imageView);
        }
    }


    @Override
    public int getItemCount() {
        if (fromNetwork) {
            if (mData == null) return 0;
            return mData.size();
        }
        else {
            if (mCursor == null) return 0;
            return mCursor.getCount();
        }
    }

    public class LoadMoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView imageView;

        LoadMoviesAdapterViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.movieImage);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            Intent openDetailsIntent = new Intent(mContext, DetailActivity.class);
            openDetailsIntent.putExtra(INTENT_FROMNETWORK, fromNetwork);

            MovieObject movie;
            if (fromNetwork) {
                movie = mData.get(adapterPosition);
            }
            else {
                if (!mCursor.moveToPosition(adapterPosition))
                    return;
                int movie_id = mCursor.getInt(mCursor.getColumnIndex(MovieDbContract.MovieDbEntry.COLUMN_MOVIE_ID)) ;
                double movie_vote = mCursor.getDouble(mCursor.getColumnIndex(MovieDbContract.MovieDbEntry.COLUMN_MOVIE_RATING));
                String movie_title = mCursor.getString(mCursor.getColumnIndex(MovieDbContract.MovieDbEntry.COLUMN_MOVIE_TITLE)) ;
                String movie_overview = mCursor.getString(mCursor.getColumnIndex(MovieDbContract.MovieDbEntry.COLUMN_MOVIE_OVERVIEW)) ;
                String movie_release_date = mCursor.getString(mCursor.getColumnIndex(MovieDbContract.MovieDbEntry.COLUMN_MOVIE_RELEASE_DATE)) ;

                movie = new MovieObject(
                        0,
                        movie_id,
                        false,
                        movie_vote,
                        movie_title,
                        0,
                        "",
                        "unknown",
                        "unknown",
                        new int[]{0},
                        "",
                        true,
                        movie_overview,
                        movie_release_date);
            }
            openDetailsIntent.putExtra(INTENT_MOVIE, movie);

            mContext.startActivity(openDetailsIntent);
        }
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }



    public void setMovieData(ArrayList<MovieObject> movieData) {
        mData = movieData;
        fromNetwork = true;
        notifyDataSetChanged();
    }

    public void setMovieData(Cursor cursorData) {
        mCursor = cursorData;
        fromNetwork = false;
        notifyDataSetChanged();
    }
}
