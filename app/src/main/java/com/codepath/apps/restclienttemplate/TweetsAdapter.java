package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets){
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> tweets){
        this.tweets.addAll(tweets);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout container;
        TextView tvUsername;
        TextView tvHandle;
        TextView tvBody;
        TextView tvTimestamp;
        ImageView ivProfilepic;
        ImageView ivUrl;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvHandle = itemView.findViewById(R.id.tvHandle);
            tvBody = itemView.findViewById(R.id.tvBody);
            ivProfilepic = itemView.findViewById(R.id.ivProfilepic);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivUrl = itemView.findViewById(R.id.ivUrl);
        }

        public void bind(final Tweet tweet) {
            tvUsername.setText(tweet.user.username);
            tvBody.setText(tweet.body);
            tvHandle.setText("@" + tweet.user.handle);
            Glide.with(context)
                    .load(tweet.user.ivProfileUrl)
                    .circleCrop()
                    .into(ivProfilepic);
            tvTimestamp.setText("- " + Tweet.getRelativeTimeAgo(tweet.createdAt));

            if(tweet.mediaUrl != ""){
                Glide.with(context)
                        .load(tweet.mediaUrl)
                        .transform(new RoundedCorners(30))
                        .into(ivUrl);
            }
            else{
                ivUrl.setVisibility(View.GONE);
            }

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, DetailActivity.class);
                    i.putExtra("Tweet", Parcels.wrap(tweet));
                    context.startActivity(i);
                }
            });
        }
    }
}
