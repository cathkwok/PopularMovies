package com.cathykwok.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE_DATA = "com.cathykwok.popularmovies.MOVIE_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_fragment);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class DetailFragment extends Fragment {
        private MovieData mMovieData;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(EXTRA_MOVIE_DATA)) {
                mMovieData = intent.getParcelableExtra(EXTRA_MOVIE_DATA);
                ((TextView) rootView.findViewById(R.id.movie_title))
                        .setText(mMovieData.getOriginalTitle());

                ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_image);
                Uri builtUri = Uri.parse(Constants.MOVIE_DB_IMAGE_BASE_URL).buildUpon()
                        .appendEncodedPath(mMovieData.getMoviePosterImage())
                        .build();
                Picasso.with(getActivity()).load(builtUri.toString()).into(imageView);

                try {
                    String releaseDate = mMovieData.getReleaseDate();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD");
                    Date date = dateFormat.parse(releaseDate);
                    dateFormat = new SimpleDateFormat("yyyy");
                    String year = dateFormat.format(date);
                    ((TextView) rootView.findViewById(R.id.movie_year)).setText(year);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String rating = getResources().getString(R.string.rating_out_of_ten, mMovieData.getUserRating());
                ((TextView) rootView.findViewById(R.id.movie_rating))
                        .setText(rating);
                ((TextView) rootView.findViewById(R.id.movie_plot))
                        .setText(mMovieData.getPlotSynopsis());
            }
            return rootView;
        }
    }
}
