package com.example.ujjwaljain.popularmovies;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private String[] movieDetails;
    private String trailerJsonString;
    private String reviewJsonString;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RecyclerView mRecyclerViewReview;
    private RecyclerView.Adapter mAdapterReview;
    private RecyclerView.LayoutManager mLayoutManagerReview;

    private ArrayList<String> mTrailers = new ArrayList<String>();
    private ArrayList<String> mReviews = new ArrayList<String>();

    boolean favMovie = false;

    View mRootView;

    public DetailActivityFragment() {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(), FavouriteMovieProvider.FavouriteMovies.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mRootView = rootView;

        String movieJson = getActivity().getIntent().getExtras().getString("Json String");
        int position = getActivity().getIntent().getExtras().getInt("Position");
        boolean fromFav = getActivity().getIntent().getExtras().getBoolean("FromFavourite");

        if (fromFav)
        {
            String movie_id = getActivity().getIntent().getExtras().getString("MovieId");

            movieDetails = FetchDetails(movie_id);
        }
        else {
            try {

                Log.v(LOG_TAG, movieJson);
                movieDetails = getImagePathsFromJson(movieJson, position);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error getting string", e);
            }
        }

        FetchTrailers fetchTrailers = new FetchTrailers();
        fetchTrailers.execute(movieDetails[6]);

        FetchReviews fetchReviews = new FetchReviews();
        fetchReviews.execute(movieDetails[6]);

        String posterUrl = "http://image.tmdb.org/t/p/w342/" + movieDetails[0];
        String backdropUrl = "http://image.tmdb.org/t/p/w342/" + movieDetails[1];

        final ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster);
        final ImageView backdropImageView = (ImageView) rootView.findViewById(R.id.backdrop);

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

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TrailerAdapter(getActivity(), mTrailers);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerViewReview = (RecyclerView) rootView.findViewById(R.id.recyclerViewReviews);
        mRecyclerViewReview.setHasFixedSize(true);
        mLayoutManagerReview = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewReview.setLayoutManager(mLayoutManagerReview);
        mAdapterReview = new ReviewAdapter(getActivity(), mReviews);
        mRecyclerViewReview.setAdapter(mAdapterReview);

        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        favMovie = isFavourite(movieDetails[6]);
        if (favMovie)
        {
            fab.setImageResource(R.drawable.star);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (favMovie) {
                    fab.setImageResource(R.drawable.star_outline);
                    getActivity().getContentResolver().delete(FavouriteMovieProvider.FavouriteMovies.withId(movieDetails[6]),
                            null, null);
                    Toast.makeText(getActivity(), "Removed from favourites", Toast.LENGTH_SHORT).show();
                }
                else {

                    fab.setImageResource(R.drawable.star);
                    ContentValues cv = new ContentValues();
                    cv.put(FavouriteMovieColumns.MOVIE_ID, movieDetails[6]);
                    cv.put(FavouriteMovieColumns.BACKDROP, movieDetails[1]);
                    cv.put(FavouriteMovieColumns.POSTER, movieDetails[0]);
                    cv.put(FavouriteMovieColumns.TITLE, movieDetails[2]);
                    cv.put(FavouriteMovieColumns.DATE, movieDetails[3]);
                    cv.put(FavouriteMovieColumns.RATING, movieDetails[4]);
                    cv.put(FavouriteMovieColumns.OVERVIEW, movieDetails[5]);

                    getActivity().getContentResolver().insert(FavouriteMovieProvider.FavouriteMovies.CONTENT_URI,
                            cv);

                    Toast.makeText(getActivity(), "Added to Favourites", Toast.LENGTH_SHORT).show();
                }

                favMovie = !favMovie;
            }
        });

        return rootView;
    }

    public boolean isFavourite(String movieId)
    {
        String[] selectionArgs = { movieId };
        Cursor c = getActivity().getContentResolver().query(FavouriteMovieProvider.FavouriteMovies.CONTENT_URI,
                null, FavouriteMovieColumns.MOVIE_ID + " = ?", selectionArgs, null);

        if (c==null || c.getCount() == 0)
            return false;
        else {
            c.moveToFirst();
            Log.v(LOG_TAG, c.getString(c.getColumnIndex(FavouriteMovieColumns.TITLE)) + movieId);
            return true;
        }
    }

    public String[] FetchDetails(String movieId)
    {
        String[] resultStr = new String[7];

        String[] selectionArgs = { movieId };
        Cursor c = getActivity().getContentResolver().query(FavouriteMovieProvider.FavouriteMovies.CONTENT_URI,
                null, FavouriteMovieColumns.MOVIE_ID + " = ?", selectionArgs, null);

        if (c==null || c.getCount()==0)
            Log.v(LOG_TAG, "No data available");
        else {
            c.moveToFirst();

            resultStr[0] = c.getString(c.getColumnIndex(FavouriteMovieColumns.POSTER));
            resultStr[1] = c.getString(c.getColumnIndex(FavouriteMovieColumns.BACKDROP));
            resultStr[2] = c.getString(c.getColumnIndex(FavouriteMovieColumns.TITLE));
            resultStr[3] = c.getString(c.getColumnIndex(FavouriteMovieColumns.DATE));
            resultStr[4] = c.getString(c.getColumnIndex(FavouriteMovieColumns.RATING));
            resultStr[5] = c.getString(c.getColumnIndex(FavouriteMovieColumns.OVERVIEW));

        }

        resultStr[6] = movieId;

        c.close();

        return resultStr;
    }

    public String[] getImagePathsFromJson(String jsonStr, int position) throws JSONException {

        String[] resultStrs = new String[7];

        JSONObject movieJson = new JSONObject(jsonStr);
        JSONArray movieArray = movieJson.getJSONArray("results");

        JSONObject reqdMovie = movieArray.getJSONObject(position);

        resultStrs[0] = reqdMovie.getString("poster_path");
        resultStrs[1] = reqdMovie.getString("backdrop_path");
        resultStrs[2] = reqdMovie.getString("title");
        resultStrs[3] = reqdMovie.getString("release_date");
        resultStrs[4] = reqdMovie.getString("vote_average");
        resultStrs[5] = reqdMovie.getString("overview");
        resultStrs[6] = reqdMovie.getString("id");

        return resultStrs;
    }

    public class FetchTrailers extends AsyncTask<String, Void, ArrayList<String>>
    {

        private String LOG_TAG = FetchTrailers.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            OkHttpClient client = new OkHttpClient();
            ArrayList<String> trailerKey;

            try {

                String baseUrl = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos?";

                Uri uri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(uri.toString());

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();

                trailerJsonString = response.body().string();

                trailerKey = getTrailersKeyFromJson(trailerJsonString);

                return trailerKey;
            }
            catch (Exception e)
            {
                Log.v(LOG_TAG, "Some problem occurred " + e.toString());
            }

            return null;
        }

        private ArrayList<String> getTrailersKeyFromJson(String trailerJson) throws JSONException
        {
            ArrayList<String> arrTrailer = new ArrayList<String>();

            JSONObject trailer = new JSONObject(trailerJson);
            JSONArray arrJson = trailer.getJSONArray("results");

            for (int i = 0; i < arrJson.length(); i++)
            {

                JSONObject singleTrailer = arrJson.getJSONObject(i);
                arrTrailer.add(singleTrailer.getString("key"));

            }

            return arrTrailer;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);

            mTrailers.addAll(strings);

            if (mTrailers.size() == 0)
            {
                mRecyclerView.setVisibility(View.GONE);
                TextView trailer = (TextView) mRootView.findViewById(R.id.trailer);
                trailer.setVisibility(View.GONE);
                View line = (View) mRootView.findViewById(R.id.split_line_2);
                line.setVisibility(View.GONE);

            }

            mAdapter.notifyDataSetChanged();
        }
    }

    public class FetchReviews extends AsyncTask<String, Void, ArrayList<String>>
    {

        private String LOG_TAG = FetchReviews.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            OkHttpClient client = new OkHttpClient();
            ArrayList<String> reviewKey;

            try {

                String baseUrl = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews?";

                Uri uri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(uri.toString());

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();

                reviewJsonString = response.body().string();

                reviewKey = getReviewsKeyFromJson(reviewJsonString);

                return reviewKey;
            }
            catch (Exception e)
            {
                Log.v(LOG_TAG, "Exception occurred " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            mReviews.addAll(strings);

            if (mReviews.size() == 0)
            {
                mRecyclerViewReview.setVisibility(View.GONE);
                TextView review = (TextView) mRootView.findViewById(R.id.review);
                review.setVisibility(View.GONE);
                View split_line = (View) mRootView.findViewById(R.id.split_line_3);
                split_line.setVisibility(View.GONE);

            }

            mAdapterReview.notifyDataSetChanged();
        }

        private ArrayList<String> getReviewsKeyFromJson(String reviewJson) throws JSONException
        {

            ArrayList<String> arrReview = new ArrayList<String>();

            JSONObject review = new JSONObject(reviewJson);
            JSONArray arrJson = review.getJSONArray("results");

            for (int i =0; i < arrJson.length(); i++)
            {

                JSONObject singleReview = arrJson.getJSONObject(i);
                arrReview.add(singleReview.getString("content"));
                arrReview.add(singleReview.getString("author"));
            }

            return arrReview;
        }
    }
}
