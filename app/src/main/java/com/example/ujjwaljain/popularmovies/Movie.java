package com.example.ujjwaljain.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ujjwal Jain on 13-12-2015.
 */
public class Movie implements Parcelable {

    String moviePath;

    public Movie(String path)
    {
        this.moviePath = path;
    }

    private Movie(Parcel in) {
        moviePath = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(moviePath);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}
