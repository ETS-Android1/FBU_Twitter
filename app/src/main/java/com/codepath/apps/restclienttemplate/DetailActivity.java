package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Headers;

public class DetailActivity extends AppCompatActivity {

    public static final String TAG = "DetailActivity";
    Tweet tweet;
    TwitterClient client;
    ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        client = TwitterApp.getRestClient(this);

        binding.ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tweet.favoriteStatus == true){
                    client.unFavoriteTweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Unfavorited tweet onSuccess");
                            tweet.favoriteStatus = false;
                            Integer numFavorites;
                            String [] split = binding.tvLikes.getText().toString().split(" ");
                            numFavorites = Integer.valueOf(split[0]);
                            binding.tvLikes.setText(String.format("%d Likes", numFavorites - 1));
                            changeButtons();
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "OnFailure to unfavorite: " + throwable.getMessage());
                        }
                    });
                }else {
                    client.favoriteTweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Favorited Tweet onSuccess");
                            tweet.favoriteStatus = true;
                            Integer numFavorites;
                            String [] split = binding.tvLikes.getText().toString().split(" ");
                            numFavorites = Integer.valueOf(split[0]);
                            binding.tvLikes.setText(String.format("%d Likes", numFavorites + 1));
                            changeButtons();
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "Favorited Tweet OnFailure: " + throwable.getMessage());
                        }
                    });
                }
            }
        });

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("Tweet"));
        Log.d(TAG, "Tweet ID: " + tweet.id);
        loadTweet();
    }

    private void loadTweet() {
        binding.tvUsername.setText(tweet.user.username);
        binding.tvHandle.setText("@" + tweet.user.handle);
        Glide.with(this)
                .load(tweet.user.ivProfileUrl)
                .circleCrop()
                .into(binding.ivProfile);

        binding.tvBody.setText(tweet.body);
        binding.tvDate.setText(formattedDate(tweet.createdAt));
        binding.tvRetweets.setText(String.format("%d Retweets", tweet.retweets));
        binding.tvLikes.setText(String.format("%d Likes", tweet.favorites));

        changeButtons();
    }

    public void changeButtons(){
        if(tweet.retweetStatus == true){
            binding.ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
            binding.ibRetweet.getDrawable().setTint(Color.RED);
        }else {
            binding.ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            binding.ibRetweet.getDrawable().setTint(Color.GRAY);
        }

        if(tweet.favoriteStatus == true){
            binding.ibFavorite.setImageResource(R.drawable.ic_vector_heart);
            binding.ibFavorite.getDrawable().setTint(Color.RED);
        }
        else{
            binding.ibFavorite.setImageResource(R.drawable.ic_vector_heart_stroke);
            binding.ibFavorite.getDrawable().setTint(Color.GRAY);
        }
    }

    private String formattedDate(String rawDate){
        StringBuilder result = new StringBuilder();
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        Date date = new Date(rawDate);
        result.append(formatter.format(date));
        formatter = new SimpleDateFormat("MM/dd/YYYY");
        result.append(" - " + formatter.format(date));
        return result.toString();
    }
}