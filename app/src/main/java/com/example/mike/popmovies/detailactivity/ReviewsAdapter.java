package com.example.mike.popmovies.detailactivity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mike.popmovies.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Mike on 2/22/2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.LoadReviewViewHolder> {


    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<JSONObject> mData;

    public ReviewsAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ReviewsAdapter.LoadReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_review, parent, false);
        return new ReviewsAdapter.LoadReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.LoadReviewViewHolder holder, int position) {
        try {
            String author = mData.get(position).getString("author");
            String content = mData.get(position).getString("content");

            holder.tvAuthor.setText(author);
            holder.tvContent.setText(content);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;
        return mData.size();
    }



    public class LoadReviewViewHolder extends RecyclerView.ViewHolder{
        public final TextView tvAuthor;
        public final TextView tvContent;

        public LoadReviewViewHolder(View view) {
            super(view);
            tvAuthor = view.findViewById(R.id.tv_author);
            tvContent = view.findViewById(R.id.tv_review);
        }

    }

    public void setData(List<JSONObject> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }
}
