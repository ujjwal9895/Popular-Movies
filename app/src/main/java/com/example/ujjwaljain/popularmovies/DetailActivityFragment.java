package com.example.ujjwaljain.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private String[] movieDetails;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        String movieJson = getActivity().getIntent().getExtras().getString("Json String");
        int position = getActivity().getIntent().getExtras().getInt("Position");

        try {
            movieDetails = getImagePathsFromJson(movieJson, position);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error getting string", e);
        }

        String posterUrl = "http://image.tmdb.org/t/p/w342/" + movieDetails[0];
        String backdropUrl = "http://image.tmdb.org/t/p/w342/" + movieDetails[1];

        ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster);
        ImageView backdropImageView = (ImageView) rootView.findViewById(R.id.backdrop);

        Picasso.with(getActivity())
                .load(posterUrl)
                .noPlaceholder()
                .into(posterImageView);

        Picasso.with(getActivity())
                .load(backdropUrl)
                .noPlaceholder()
                .into(backdropImageView);

        TextView titleTextView = (TextView) rootView.findViewById(R.id.movie_title);
        TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.releaseDate);
        TextView ratingTextView = (TextView) rootView.findViewById(R.id.rating);
        TextView overviewTextView = (TextView) rootView.findViewById(R.id.movie_overview);

        titleTextView.setText(movieDetails[2]);
        releaseDateTextView.setText(movieDetails[3]);
        ratingTextView.setText(movieDetails[4]);
        overviewTextView.setText(movieDetails[5]);

        return rootView;
    }

    public String[] getImagePathsFromJson(String jsonStr, int position) throws JSONException {

        String[] resultStrs = new String[6];

        JSONObject movieJson = new JSONObject(jsonStr);
        JSONArray movieArray = movieJson.getJSONArray("results");

        JSONObject reqdMovie = movieArray.getJSONObject(position);

        resultStrs[0] = reqdMovie.getString("poster_path");
        resultStrs[1] = reqdMovie.getString("backdrop_path");
        resultStrs[2] = reqdMovie.getString("title");
        resultStrs[3] = reqdMovie.getString("release_date");
        resultStrs[4] = reqdMovie.getString("vote_average");
        resultStrs[5] = reqdMovie.getString("overview");

        return resultStrs;
    }
}
