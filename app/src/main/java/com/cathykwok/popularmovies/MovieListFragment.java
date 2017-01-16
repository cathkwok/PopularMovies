package com.cathykwok.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MovieListFragment extends Fragment implements FetchMovieTask.FetchMovieTaskResponse{

    @BindView(R.id.movie_grid_view) GridView mMovieGridView;

    private MovieListAdapter mMovieListAdapter;
    private SharedPreferences mSharedPreferences;
    private SubMenu mSortMenu;
    private String mSortType;

    private Unbinder unbinder;

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
        unbinder = ButterKnife.bind(this, rootView);

        mMovieListAdapter = new MovieListAdapter(getActivity());
        mMovieGridView.setAdapter(mMovieListAdapter);

        mMovieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                MovieData movieData = mMovieListAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(DetailFragment.EXTRA_MOVIE_DATA, movieData);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateMovieList() {
        if (!Utils.isNetworkConnected(getActivity())) {
            displayNoNetworkDialog();
        } else {
            FetchMovieTask movieTask = new FetchMovieTask(this);
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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


    @Override
    public void processFinish(MovieData[] data) {
        mMovieListAdapter.updateMovieList(new ArrayList<>(Arrays.asList(data)));
        if (mMovieGridView != null) {
            mMovieGridView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void processError() {
        Toast.makeText(getContext(), R.string.movie_loading_error, Toast.LENGTH_SHORT);
    }
}
