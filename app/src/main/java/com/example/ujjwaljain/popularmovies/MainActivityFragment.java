package com.example.ujjwaljain.popularmovies;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayList<Movie> moviePosterPaths;
    private String sortOrder;
    private ImageAdapter imageAdapter;
    private String movieJsonString;
    private final String STATE_MOVIES = "state_movies";
    private final String SORT_CRITERIA = "sort_criteria";
    private final String SAVED_STRING = "json_string";

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sortOrder = "popularity.desc";
        setHasOptionsMenu(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_MOVIES)) {
            moviePosterPaths = savedInstanceState.getParcelableArrayList(STATE_MOVIES);
        } else {
            moviePosterPaths = new ArrayList<>();
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(SORT_CRITERIA)) {
            sortOrder = savedInstanceState.getString(SORT_CRITERIA);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_STRING)) {
            movieJsonString = savedInstanceState.getString(SAVED_STRING);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_sort_popularity) {

            sortOrder = "popularity.desc";
            updateMovies();
            return true;

        }

        if (id == R.id.action_sort_rating) {

            sortOrder = "vote_average.desc";
            updateMovies();
            return true;

        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (moviePosterPaths.isEmpty()) {
            updateMovies();
        }
    }

    public void updateMovies() {

        moviePosterPaths.clear();
        FetchMovies fetchMovies = new FetchMovies();
        fetchMovies.execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        imageAdapter = new ImageAdapter(getActivity(),moviePosterPaths);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("Json String", movieJsonString);
                intent.putExtra("Position", position);
                startActivity(intent);

            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_MOVIES, moviePosterPaths);
        outState.putString(SORT_CRITERIA, sortOrder);
        outState.putString(SAVED_STRING, movieJsonString);
    }

    public class FetchMovies extends AsyncTask<Void, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        @Override
        protected ArrayList<Movie> doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient();

            ArrayList<Movie> moviesPoster;

            try {

                String base_URL = "http://api.themoviedb.org/3/discover/movie?";

                Uri uri = Uri.parse(base_URL).buildUpon()
                        .appendQueryParameter("sort_by", sortOrder)
                        .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(uri.toString());

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();

                movieJsonString = response.body().string();

                Log.v(LOG_TAG, "Json string" + movieJsonString);

                moviesPoster = getMoviesFromJson(movieJsonString);

            }catch (IOException e) {

                Log.e(LOG_TAG, "Error closing stream", e);
                return null;

            }catch (Exception e) {

                Log.e(LOG_TAG, "Error occurred", e);
                return null;

            }

            return moviesPoster;
        }

        private ArrayList<Movie> getMoviesFromJson(String movieJson) throws JSONException {

            ArrayList<Movie> moviesList = new ArrayList<Movie>();
            JSONObject movies = new JSONObject(movieJson);
            JSONArray movieArray = movies.getJSONArray("results");

            for (int i = 0; i < movieArray.length(); i++) {

                JSONObject singleMovie = movieArray.getJSONObject(i);
                moviesList.add(new Movie(singleMovie.getString("poster_path")));

            }
            return moviesList;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> moviePosterArray) {

            moviePosterPaths.addAll(moviePosterArray);
            imageAdapter.notifyDataSetChanged();

        }
    }
}
