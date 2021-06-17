package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Headers;

public class DetailActivity extends FragmentActivity {

    public static final String TAG = "DetailActivity";
    Tweet tweet;
    TextView tvUsername;
    TextView tvHandle;
    TextView tvBody;
    TextView tvDate;
    TextView tvRetweets;
    TextView tvFavorites;
    ImageView ivProfile;
    ImageButton ibRetweet;
    ImageButton ibFavorite;
    TwitterClient client;
    RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("Tweet"));
        client = TwitterApp.getRestClient(this);
        container = findViewById(R.id.container);
        tvBody = findViewById(R.id.tvBody);
        tvHandle = findViewById(R.id.tvHandle);
        tvUsername = findViewById(R.id.tvUsername);
        tvDate = findViewById(R.id.tvDate);
        ivProfile = findViewById(R.id.rvUser);
        tvRetweets = findViewById(R.id.tvRetweets);
        tvFavorites = findViewById(R.id.tvLikes);
        ibFavorite = findViewById(R.id.ibFavorite);
        ibRetweet = findViewById(R.id.ibRetweet);
        ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tweet.favoriteStatus == true){
                    client.unFavoriteTweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Unfavorited tweet onSuccess");
                            tweet.favoriteStatus = false;
                            Integer numFavorites;
                            String [] split = tvFavorites.getText().toString().split(" ");
                            numFavorites = Integer.valueOf(split[0]);
                            tvFavorites.setText(String.format("%d Likes", numFavorites - 1));
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
                            String [] split = tvFavorites.getText().toString().split(" ");
                            numFavorites = Integer.valueOf(split[0]);
                            tvFavorites.setText(String.format("%d Likes", numFavorites + 1));
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

        // TODO: Add ability to retweet/un-retweet
        // Set an onClickListener for the retweet button

        // TODO: Ability to see followers list
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchFollowers();
            }
        });

        loadTweet();
    }

    private void launchFollowers() {
        Intent i = new Intent(DetailActivity.this, ProfileActivity.class);
        i.putExtra("id_str", tweet.user.id_str);
        startActivity(i);
    }

    private void loadTweet() {
        tvUsername.setText(tweet.user.username);
        tvHandle.setText("@" + tweet.user.handle);
        Glide.with(this)
                .load(tweet.user.ivProfileUrl)
                .circleCrop()
                .into(ivProfile);

        tvBody.setText(tweet.body);
        tvDate.setText(formattedDate(tweet.createdAt));
        tvRetweets.setText(String.format("%d Retweets", tweet.retweets));
        tvFavorites.setText(String.format("%d Likes", tweet.favorites));

        changeButtons();
    }

    public void changeButtons(){
        if(tweet.retweetStatus == true){
            ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
            ibRetweet.getDrawable().setTint(Color.RED);
        }else {
            ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            ibRetweet.getDrawable().setTint(Color.GRAY);
        }

        if(tweet.favoriteStatus == true){
            ibFavorite.setImageResource(R.drawable.ic_vector_heart);
            ibFavorite.getDrawable().setTint(Color.RED);
        }
        else{
            ibFavorite.setImageResource(R.drawable.ic_vector_heart_stroke);
            ibFavorite.getDrawable().setTint(Color.GRAY);
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