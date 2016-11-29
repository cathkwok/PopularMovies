package com.cathykwok.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MovieListFragment extends Fragment {

    private MovieListAdapter mMovieListAdapter;
    private GridView mMovieGridView;
    private SharedPreferences mSharedPreferences;
    private SubMenu mSortMenu;
    private String mSortType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSortType = mSharedPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popular));
        updateMovieList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_popular:
                setSortType(getString(R.string.pref_sort_popular));
                updateMovieList();
                return true;

            case R.id.action_sort_rating:
                setSortType(getString(R.string.pref_sort_top_rated));
                updateMovieList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        mMovieGridView = (GridView) rootView.findViewById(R.id.movie_grid_view);

        mMovieListAdapter = new MovieListAdapter(getActivity());
        mMovieGridView.setAdapter(mMovieListAdapter);

        mMovieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                MovieData movieData = mMovieListAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(DetailActivity.EXTRA_MOVIE_DATA, movieData);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateMovieList() {
        if (!Utils.isNetworkConnected(getActivity())) {
            displayNoNetworkDialog();
        } else {
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute(mSortType);
        }
    }

    private void displayNoNetworkDialog() {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(getActivity()).create();
        alertDialog.setMessage(getResources().getString(R.string.no_network_msg));
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_list_fragment, menu);
        if (menu.getItem(0) != null) {
            mSortMenu = menu.getItem(0).getSubMenu();
        }
        setOptionsMenu();
    }


    private void setOptionsMenu() {
        int selectedSortId = mSortType == getString(R.string.pref_sort_popular) ? R.id.action_sort_popular : R.id.action_sort_rating;
        for (int i = 0; i < mSortMenu.size(); i++) {
            MenuItem menuItem = mSortMenu.getItem(i);
            int iconId = menuItem.getItemId() == (selectedSortId) ? R.drawable.ic_done_black_24dp : 0;
            menuItem.setIcon(iconId);
        }
    }

    private void setSortType(String sortType) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(getString(R.string.pref_sort_key), sortType);
        editor.commit();
        mSortType = sortType;
        setOptionsMenu();
    }

    public class MovieListAdapter extends ArrayAdapter<MovieData> {

        private ArrayList<MovieData> mMovieList = new ArrayList<>();


        public MovieListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public int getCount() {
            return mMovieList.size();
        }

        @Override
        public MovieData getItem(int position) {
            return mMovieList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(getContext());
                imageView.setLayoutParams(new GridView.LayoutParams((int) getResources().getDimension(R.dimen.movie_thumbnail_width), (int) getResources().getDimension(R.dimen.movie_thumbnail_height)));
            } else {
                imageView = (ImageView) convertView;
            }

            MovieData movieData = getItem(position);

            Uri builtUri = Uri.parse(Constants.MOVIE_DB_IMAGE_BASE_URL).buildUpon()
                    .appendEncodedPath(movieData.getMoviePosterImage())
                    .build();

            Picasso.with(getContext()).load(builtUri.toString()).into(imageView);
            return imageView;
        }

        public void updateMovieList(ArrayList<MovieData> movieDataList) {
            mMovieList = movieDataList;
            notifyDataSetChanged();
            if (mMovieGridView != null) {
                mMovieGridView.smoothScrollToPosition(0);
            }
        }
    }

    public class FetchMovieTask extends AsyncTask<String, Void, MovieData[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

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
                mMovieListAdapter.updateMovieList(new ArrayList<>(Arrays.asList(result)));
            }
        }
    }
}
