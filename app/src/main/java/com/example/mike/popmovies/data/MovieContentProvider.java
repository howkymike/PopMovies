package com.example.mike.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.mike.popmovies.data.MovieDbContract.MovieDbEntry.TABLE_NAME;

/**
 * Created by Mike on 2/27/2018.
 */

public class MovieContentProvider extends ContentProvider{

    public static final int TASKS = 100;
    public static final int TASK_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieDbContract.AUTHORITY, MovieDbContract.PATH_TASKS, TASKS);
        uriMatcher.addURI(MovieDbContract.AUTHORITY, MovieDbContract.PATH_TASKS + "/#", TASK_WITH_ID);

        return uriMatcher;
    }

    private MovieDbHelper mMovieDbHelper;


    @Override
    public boolean onCreate() {

        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        Log.v("I'm here", "before: ");

        int match = sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (match) {
            case TASKS:
                Log.v("I'm here", "match: " + match);
                returnCursor = db.query(
                        TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TASKS:
                long id = db.insert(MovieDbContract.MovieDbEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieDbContract.MovieDbEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into ");
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int tasksDeleted; // starts as 0
        int match = sUriMatcher.match(uri);

        switch (match) {
            case TASK_WITH_ID:
                String id = uri.getPathSegments().get(1);
                //tasksDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                tasksDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
