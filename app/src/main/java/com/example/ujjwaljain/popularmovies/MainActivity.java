package com.example.ujjwaljain.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{

    public boolean mTwoPane;
    private String DetailActivityTag = DetailActivityFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null)
        {
            mTwoPane = true;

            if (savedInstanceState == null) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment())
                        .commit();

            }
        }
        else {
            mTwoPane = false;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public void onItemSelected(String jsonStr, int position, boolean fromFav, String movie_id) {

        if (mTwoPane)
        {
            Bundle args = new Bundle();
            args.putString("Json String", jsonStr);
            args.putInt("Position", position);
            args.putBoolean("FromFavourite", fromFav);
            args.putString("MovieId", movie_id);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DetailActivityTag)
                    .commit();
        }
        else {

            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("Json String", jsonStr);
            intent.putExtra("Position", position);
            intent.putExtra("FromFavourite", fromFav);
            intent.putExtra("MovieId", movie_id);

            startActivity(intent);
        }
    }
}
