package com.cathykwok.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cathykwok on 2016-12-10.
 */

public class FetchMovieTask extends AsyncTask<String, Void, MovieData[]> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    public interface FetchMovieTaskResponse {
        void processFinish(MovieData[] data);
    }

    public FetchMovieTaskResponse delegate = null;

    public FetchMovieTask(FetchMovieTaskResponse delegate){
        this.delegate = delegate;
    }

    private MovieData[] getMovieDataFromJson(String movieJsonStr) throws JSONException {
        final String MOVIE_DB_RESULTS = "results";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(MOVIE_DB_RESULTS);

        MovieData[] resultStrs = new MovieData[movieArray.length()];

        MovieDataJsonParser jsonParser = new MovieDataJsonParser();

        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieUrlObject = movieArray.getJSONObject(i);

            resultStrs[i] = jsonParser.fromJson(movieUrlObject);
        }
        return resultStrs;

    }

    @Override
    protected MovieData[] doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;

        try {
            Uri builtUri = Uri.parse(Constants.MOVIE_DB_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter(Constants.API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            movieJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(MovieData[] result) {
        if (result != null) {
            delegate.processFinish(result);
        }
    }
}
