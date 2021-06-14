package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    public static final String TAG = "DetailActivity";
    Tweet tweet;
    TextView tvUsername;
    TextView tvHandle;
    TextView tvBody;
    TextView tvDate;
    ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvBody = findViewById(R.id.tvBody);
        tvHandle = findViewById(R.id.tvHandle);
        tvUsername = findViewById(R.id.tvUsername);
        tvDate = findViewById(R.id.tvDate);
        ivProfile = findViewById(R.id.ivProfile);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("Tweet"));
        Log.d(TAG, "Tweet ID: " + tweet.id);
        loadTweet();
    }

    private void loadTweet() {
        tvUsername.setText(tweet.user.username);
        tvHandle.setText("@" + tweet.user.handle);
        Glide.with(this)
                .load(tweet.user.ivProfileUrl)
                .circleCrop()
                .into(ivProfile);

        tvBody.setText(tweet.body);
        tvDate.setText(Tweet.getRelativeTimeAgo(tweet.createdAt));
    }
}