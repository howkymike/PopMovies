package com.example.mike.popmovies.detailactivity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mike.popmovies.Networking.NetworkUtils;
import com.example.mike.popmovies.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

/**
 * Created by Mike on 2/21/2018.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.LoadTrailerViewHolder>{

    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<JSONObject> mData;

    public TrailersAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public LoadTrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_trailerview, parent, false);
        return new LoadTrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LoadTrailerViewHolder holder, int position) {
        try {
            String site = mData.get(position).getString("site");
            String name = mData.get(position).getString("name");

            if (!site.equals("YouTube")) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.no_youtube_trailer), Toast.LENGTH_LONG).show();
                return;
            }

            holder.tv_trailer.setText(name);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;
        return mData.size();
    }

    public class LoadTrailerViewHolder extends RecyclerView.ViewHolder{
        public final TextView tv_trailer;
        public final ImageButton ib_play;

        public LoadTrailerViewHolder(View view) {
            super(view);
            tv_trailer = view.findViewById(R.id.tv_trailerName);

            ib_play = view.findViewById(R.id.ib_play);
            ib_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    try {
                        String key = mData.get(adapterPosition).getString("key");
                        NetworkUtils.watchYoutubeVideo(mContext, key);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    public void setData(List<JSONObject> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

}
