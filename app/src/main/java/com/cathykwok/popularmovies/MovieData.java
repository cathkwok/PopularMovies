package com.cathykwok.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieData implements Parcelable {

    private String mOriginalTitle;
    private String mMoviePosterImage;
    private String mPlotSynopsis;
    private String mUserRating;
    private String mReleaseDate;

    public MovieData() {
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    public String getMoviePosterImage() {
        return mMoviePosterImage;
    }

    public void setMoviePosterImage(String moviePosterImage) {
        mMoviePosterImage = moviePosterImage;
    }

    public String getPlotSynopsis() {
        return mPlotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        mPlotSynopsis = plotSynopsis;
    }

    public String getUserRating() {
        return mUserRating;
    }

    public void setUserRating(String userRating) {
        mUserRating = userRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    protected MovieData(Parcel in) {
        mOriginalTitle = in.readString();
        mMoviePosterImage = in.readString();
        mPlotSynopsis = in.readString();
        mUserRating = in.readString();
        mReleaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mOriginalTitle);
        dest.writeString(mMoviePosterImage);
        dest.writeString(mPlotSynopsis);
        dest.writeString(mUserRating);
        dest.writeString(mReleaseDate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieData> CREATOR = new Parcelable.Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
}