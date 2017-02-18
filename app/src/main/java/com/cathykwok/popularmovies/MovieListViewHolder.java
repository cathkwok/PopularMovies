package com.cathykwok.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cathykwok on 2017-01-16.
 */

public class MovieListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.movie_list_image)
    ImageView mMovieImage;

    public MovieListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onClick(View view) {

    }
}
