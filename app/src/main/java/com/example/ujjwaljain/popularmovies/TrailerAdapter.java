package com.example.ujjwaljain.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by Ujjwal Jain on 15-02-2016.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder>
{

    private final String LOG_TAG = TrailerAdapter.class.getSimpleName();
    private ArrayList<String> arrTrailer;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public ImageView imageView;

        public ViewHolder(View v) {

            super(v);
            mView = v;
            imageView = (ImageView) v.findViewById(R.id.imgTrailer);
        }
    }

    public TrailerAdapter(Context context, ArrayList<String> myTrailers) {

        mContext = context;
        arrTrailer = myTrailers;
    }

    @Override
    public int getItemCount() {

        return arrTrailer.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.ViewHolder holder, final int position) {

        String ytubeUrl = "http://img.youtube.com/vi/" + arrTrailer.get(position) + "/0.jpg";

        Uri ytUri = Uri.parse(ytubeUrl);

        Picasso.with(mContext)
                .load(ytUri)
                .placeholder(R.drawable.empty_photo)
                .into(holder.imageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = v.getContext();

                String yturl = "http://www.youtube.com/watch?v=" + arrTrailer.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(yturl));
                context.startActivity(intent);

            }
        });
    }
}
