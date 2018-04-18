package com.example.mike.popmovies.Networking;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Mike on 2/17/2018.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    // TODO insert your API here
    private static final String API_KEY = "";
    private static final String LANGUAGE = "en-US";

    private final static String PARAM_API_KEY = "api_key";
    private final static String PARAM_LANGUAGE = "language";
    private final static String PARAM_PAGE = "page";

    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w500";

    private static final String theMovieDbAPIPath = "api.themoviedb.org";


    public static URL buildUrl(String typeOfRecord, int page) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .path(theMovieDbAPIPath)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(typeOfRecord)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, LANGUAGE)
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page));

        Uri addressUri = builder.build();

        URL url = null;
        try {
            url = new URL(addressUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String buildImageUrl (String image_path) {
        String imageURL = IMAGE_BASE_URL + IMAGE_SIZE + image_path;
        Log.v(TAG, "Built Image URI " + imageURL);

        return imageURL;
    }


    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            if (webIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(webIntent);
            }
            else {
                Toast.makeText(context, "Who doesn't have a browser?!?!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static JSONObject getMovieVideos(int movie_id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .path(theMovieDbAPIPath)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(String.valueOf(movie_id))
                .appendPath("videos")
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, LANGUAGE);

        Uri addressUri = builder.build();

        URL url = null;
        try {
            url = new URL(addressUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = null;
        try {
            String videosString = getResponseFromHttpUrl(url);

            try {
                jsonObject = new JSONObject(videosString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonObject;


    }


    public static JSONObject getMovieReviews(int movie_id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .path(theMovieDbAPIPath)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(String.valueOf(movie_id))
                .appendPath("reviews")
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_LANGUAGE, LANGUAGE);

        Uri addressUri = builder.build();

        URL url = null;
        try {
            url = new URL(addressUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = null;
        try {
            String videosString = getResponseFromHttpUrl(url);

            try {
                jsonObject = new JSONObject(videosString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonObject;


    }


    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
