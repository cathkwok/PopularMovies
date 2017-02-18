package com.cathykwok.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListViewHolder> {

    private ArrayList<MovieData> mMovieList = new ArrayList<>();
    private Context context;

    public MovieListAdapter(Context context) {
        this.context = context;
        super(context, 0);
    }

    /*@Override
    public int getCount() {
        return mMovieList.size();
    }

    @Override
    public MovieListViewHolder getItem(int position) {
        return mMovieList.get(position);
    }
*/
    @Override
    public MovieListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);
        return new MovieListViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(MovieData holder, int position) {

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View movieView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            movieView = inflater.inflate(R.layout.movie_list_item, parent, false);
        } else {
            movieView = convertView;
        }

        MovieData movieData = getItem(position);

        Uri builtUri = Uri.parse(Constants.MOVIE_DB_IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(movieData.getMoviePosterImage())
                .build();

        ImageView imageView = (ImageView) movieView.findViewById(R.id.movie_list_image);

        if(imageView != null) {
            Picasso.with(getContext()).load(builtUri.toString()).into(imageView);
        }

        return movieView;
    }

    public void updateMovieList(ArrayList<MovieData> movieDataList) {
        mMovieList = movieDataList;
        notifyDataSetChanged();
    }
}