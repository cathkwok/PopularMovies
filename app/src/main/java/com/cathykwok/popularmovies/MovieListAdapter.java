package com.cathykwok.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
            imageView.setLayoutParams(new GridView.LayoutParams((int) getContext().getResources().getDimension(R.dimen.movie_thumbnail_width), (int) getContext().getResources().getDimension(R.dimen.movie_thumbnail_height)));
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
    }
}