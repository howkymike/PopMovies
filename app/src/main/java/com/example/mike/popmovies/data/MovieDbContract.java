package com.example.mike.popmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mike on 2/23/2018.
 */

public class MovieDbContract {


    public static final String AUTHORITY = "com.example.mike.popmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_TASKS = "favouriteMovies";


    public static final class MovieDbEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public static final String TABLE_NAME = "favouriteMovies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
    }
}
