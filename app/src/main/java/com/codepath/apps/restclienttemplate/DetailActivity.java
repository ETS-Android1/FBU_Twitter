package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    public static final String TAG = "DetailActivity";
    Tweet tweet;
    TextView tvUsername;
    TextView tvHandle;
    TextView tvBody;
    TextView tvDate;
    TextView tvRetweets;
    TextView tvFavorites;
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
        tvRetweets = findViewById(R.id.tvRetweets);
        tvFavorites = findViewById(R.id.tvLikes);

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
        tvDate.setText(formattedDate(tweet.createdAt));
        tvRetweets.setText(String.format("%d Retweets", tweet.retweets));
        tvFavorites.setText(String.format("%d Likes", tweet.favorites));
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