package com.cathykwok.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by cathykwok on 2016-12-10.
 */

public class DetailFragment extends Fragment {

    public static final String EXTRA_MOVIE_DATA = "com.cathykwok.popularmovies.MOVIE_DATA";

    @BindView(R.id.movie_title) TextView mTitleView;
    @BindView(R.id.movie_image) ImageView mImageView;
    @BindView(R.id.movie_year) TextView mYearView;
    @BindView(R.id.movie_rating) TextView mRatingView;
    @BindView(R.id.movie_plot) TextView mPlotView;

    private MovieData mMovieData;

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(EXTRA_MOVIE_DATA)) {
            mMovieData = intent.getParcelableExtra(EXTRA_MOVIE_DATA);
            mTitleView.setText(mMovieData.getOriginalTitle());

            Uri builtUri = Uri.parse(Constants.MOVIE_DB_IMAGE_BASE_URL).buildUpon()
                    .appendEncodedPath(mMovieData.getMoviePosterImage())
                    .build();
            Picasso.with(getActivity()).load(builtUri.toString()).into(mImageView);

            try {
                String releaseDate = mMovieData.getReleaseDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD");
                Date date = dateFormat.parse(releaseDate);
                dateFormat = new SimpleDateFormat("yyyy");
                String year = dateFormat.format(date);
                mYearView.setText(year);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String rating = getResources().getString(R.string.rating_out_of_ten, mMovieData.getUserRating());
            mRatingView.setText(rating);
            mPlotView.setText(mMovieData.getPlotSynopsis());
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
