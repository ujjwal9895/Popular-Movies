package com.example.ujjwaljain.popularmovies;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayList<String> moviePosterPaths;
    private String sortOrder;
    private ImageAdapter imageAdapter;
    private String movieJsonString;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sortOrder = "popularity.desc";
        setHasOptionsMenu(true);
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

    public void updateMovies() {

        moviePosterPaths.clear();
        FetchMovies fetchMovies = new FetchMovies();
        fetchMovies.execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        moviePosterPaths = new ArrayList<String>();
        imageAdapter = new ImageAdapter(getActivity(),moviePosterPaths);

        updateMovies();

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

    public class FetchMovies extends AsyncTask<Void, Void, ArrayList<String>> {

        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            ArrayList<String> moviesPoster;

            try {

                String base_URL = "http://api.themoviedb.org/3/discover/movie?";

                Uri uri = Uri.parse(base_URL).buildUpon()
                        .appendQueryParameter("sort_by", sortOrder)
                        .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }

                if (stringBuffer.length() == 0) {
                    return null;
                }

                movieJsonString = stringBuffer.toString();

                Log.v(LOG_TAG, movieJsonString);

                moviesPoster = getMoviesFromJson(movieJsonString);

            }catch (IOException e) {

                Log.e(LOG_TAG, "Error closing stream", e);
                return null;

            }catch (Exception e) {

                Log.e(LOG_TAG, "Error occurred", e);
                return null;

            }
            finally {

                if (urlConnection != null)
                    urlConnection.disconnect();

                if (bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    }catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing reader", e);
                    }
                }
            }

            return moviesPoster;
        }

        private ArrayList<String> getMoviesFromJson(String movieJson) throws JSONException {

            ArrayList<String> moviesList = new ArrayList<String>();
            JSONObject movies = new JSONObject(movieJson);
            JSONArray movieArray = movies.getJSONArray("results");

            for (int i = 0; i < movieArray.length(); i++) {

                JSONObject singleMovie = movieArray.getJSONObject(i);
                moviesList.add(singleMovie.getString("poster_path"));

            }
            return moviesList;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ArrayList<String> moviePosterArray) {

            moviePosterPaths.addAll(moviePosterArray);
            imageAdapter.notifyDataSetChanged();

        }
    }
}
