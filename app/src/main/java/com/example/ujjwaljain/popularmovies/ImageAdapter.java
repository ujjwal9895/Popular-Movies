package com.example.ujjwaljain.popularmovies;

import android.app.ActionBar;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private final String LOG_TAG = ImageAdapter.class.getSimpleName();

    private ArrayList<Movie> moviePathArr;

    private Context mContext;

    public ImageAdapter(Context c, ArrayList<Movie> m)
    {
        mContext = c;
        moviePathArr = m;
    }

    @Override
    public int getCount() {
        return moviePathArr.size();
    }

    @Override
    public Object getItem(int position)
    {
        if (position >= 0 && position < moviePathArr.size())
            return moviePathArr.get(position);

        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;

        if (convertView == null)
        {
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(0, 0, 0, 0);
        }
        else {
            imageView = (ImageView) convertView;
        }

        String posterURL = "http://image.tmdb.org/t/p/w342/" + moviePathArr.get(position).moviePath;

        Picasso.with(mContext)
                .load(posterURL)
                .placeholder(R.drawable.empty_photo)
                .into(imageView);

        return imageView;
    }
}
