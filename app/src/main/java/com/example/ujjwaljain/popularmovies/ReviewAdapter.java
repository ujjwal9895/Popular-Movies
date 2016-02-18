package com.example.ujjwaljain.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ujjwal Jain on 18-02-2016.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final String LOG_TAG = ReviewAdapter.class.getSimpleName();
    private ArrayList<String> arrReview;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public TextView mReview;
        public TextView mAuthor;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mReview = (TextView) itemView.findViewById(R.id.reviewTxt);
            mAuthor = (TextView) itemView.findViewById(R.id.authorName);

        }
    }

    public ReviewAdapter(Context context, ArrayList<String> myReviews) {

        mContext = context;
        arrReview = myReviews;
    }

    @Override
    public int getItemCount() {
        return arrReview.size()/2;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, int position) {

        holder.mReview.setText(arrReview.get(2*position));
        holder.mAuthor.setText(arrReview.get(2*position + 1));

    }
}
