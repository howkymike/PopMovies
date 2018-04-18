package com.example.mike.popmovies;

import java.io.Serializable;

/**
 * Created by Mike on 2/18/2018.
 */

public class MovieObject implements Serializable{

    private int vote_count;
    private int ID;
    private boolean ifVideo;
    private double vote_average;
    private String title;
    private double popularity;
    private String poster_path;
    private String original_language;
    private String original_title;
    private int[] genre_ids;
    private String backdrop_path;
    private boolean ifAdult;
    private String overview;
    private String release_date;
    private boolean favourite;


    public MovieObject(int vote_count, int ID, boolean ifVideo, double vote_average, String title, double popularity, String poster_path,
                       String original_language, String original_title, int[] genre_ids, String backdrop_path, boolean ifAdult,
                       String overview, String release_date) {
        this.vote_count = vote_count;
        this.ID = ID;
        this.ifVideo = ifVideo;
        this.vote_average = vote_average;
        this.title = title;
        this.popularity = popularity;
        this.poster_path = poster_path;
        this.original_language = original_language;
        this.original_title = original_title;
        this.genre_ids = genre_ids;
        this.backdrop_path = backdrop_path;
        this.ifAdult = ifAdult;
        this.overview = overview;
        this.release_date = release_date;
        this.favourite = false;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean isIfVideo() {
        return ifVideo;
    }

    public void setIfVideo(boolean ifVideo) {
        this.ifVideo = ifVideo;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public int[] getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(int[] genre_ids) {
        this.genre_ids = genre_ids;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public boolean isIfAdult() {
        return ifAdult;
    }

    public void setIfAdult(boolean ifAdult) {
        this.ifAdult = ifAdult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
