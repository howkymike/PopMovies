package com.example.mike.popmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mike on 2/18/2018.
 */


public class JsonUtils   {


    public static ArrayList<MovieObject> parseJson2Movies(JSONObject json) {

        int currentPage = 0;
        int total_results = 0;
        ArrayList<MovieObject> movies;

        try {
            currentPage = json.getInt("page");
            total_results = json.getInt("total_results");
            JSONArray results = json.getJSONArray("results");

            movies = new ArrayList<MovieObject>(results.length());

            for (int i = 0; i < results.length(); i++) {
                JSONObject singleMovie = results.getJSONObject(i);

                JSONArray genres = singleMovie.getJSONArray("genre_ids");
                int[] genres_ID = new int[genres.length()];
                for (int j = 0; j < genres.length(); j++) genres_ID[j] = genres.getInt(j);


                movies.add(i, new MovieObject(singleMovie.getInt("vote_count"),
                        singleMovie.getInt("id"),
                        singleMovie.getBoolean("video"),
                        singleMovie.getDouble("vote_average"),
                        singleMovie.getString("title"),
                        singleMovie.getDouble("popularity"),
                        singleMovie.getString("poster_path"),
                        singleMovie.getString("original_language"),
                        singleMovie.getString("original_title"),
                        genres_ID,
                        singleMovie.getString("backdrop_path"),
                        singleMovie.getBoolean("adult"),
                        singleMovie.getString("overview"),
                        singleMovie.getString("release_date")
                ));

            }
            return movies;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
