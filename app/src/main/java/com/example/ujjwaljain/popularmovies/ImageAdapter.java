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

    private ArrayList<String> moviePath;

    private Context mContext;

    public ImageAdapter(Context c, ArrayList<String> m)
    {
        mContext = c;
        moviePath = m;
    }

    @Override
    public int getCount() {
        return moviePath.size();
    }

    @Override
    public Object getItem(int position)
    {
        if (position >= 0 && position < moviePath.size())
            return moviePath.get(position);

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
         //   imageView.setLayoutParams(new GridView.LayoutParams
           //         (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setAdjustViewBounds(true);
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        }
        else {
            imageView = (ImageView) convertView;
        }

        Log.v(LOG_TAG, "http://image.tmdb.org/t/p/w342/" + moviePath.get(position));
        Picasso.with(mContext)
                .load("http://image.tmdb.org/t/p/w342/" + moviePath.get(position))
                .placeholder(R.drawable.empty_photo)
                .into(imageView);

        return imageView;
    }
}
