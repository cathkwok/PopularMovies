package com.cathykwok.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cathykwok on 2016-11-27.
 */

public class MovieDataJsonParser {

    private final String ORIGINAL_TITLE_KEY = "original_title";
    private final String MOVIE_POSTER_IMAGE_KEY = "poster_path";
    private final String PLOT_SYNOPSIS_KEY = "overview";
    private final String USER_RATING_KEY = "vote_average";
    private final String RELEASE_DATE_KEY = "release_date";

    public MovieData fromJson(JSONObject jsonObject) throws JSONException {
        MovieData movieData = new MovieData();
        movieData.setOriginalTitle(jsonObject.getString(ORIGINAL_TITLE_KEY));
        movieData.setMoviePosterImage(jsonObject.getString(MOVIE_POSTER_IMAGE_KEY));
        movieData.setPlotSynopsis(jsonObject.getString(PLOT_SYNOPSIS_KEY));
        movieData.setUserRating(jsonObject.getString(USER_RATING_KEY));
        movieData.setReleaseDate(jsonObject.getString(RELEASE_DATE_KEY));
        return movieData;
    }
}
