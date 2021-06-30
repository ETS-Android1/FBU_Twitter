package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.Adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.HomeActivity;
import com.codepath.apps.restclienttemplate.NewTweetListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements NewTweetListener {

    public static final String TAG = "HomeFragment";
    public static final int COMPOSE_REQUEST = 55;
    Context context;
    TweetDao tweetDao;
    SwipeRefreshLayout swipeContainer;
    List<Tweet> tweets;
    RecyclerView rvTweets;
    TwitterClient client;
    TweetsAdapter adapter;
    EndlessRecyclerViewScrollListener scrollListener;
    FloatingActionButton fabCompose;

    public HomeFragment(Context context){
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        rvTweets = view.findViewById(R.id.rvTweets);
        tweetDao = ((TwitterApp) context).getMyDatabase().tweetDao();

        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
                Log.d(TAG, "Fetching");
            }
        });

        fabCompose = view.findViewById(R.id.fabCompose);
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                ComposeFragment fragment = new ComposeFragment();
                fragment.setTargetFragment(HomeFragment.this, 300);
                fragment.show(fm, "test");
            }
        });

        client = new TwitterClient(getContext());

        tweets = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "OnLoadMore:");
                loadNextDataFromApi(page);
            }
        };

        adapter = new TweetsAdapter(getContext(), tweets);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(adapter);
        rvTweets.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        rvTweets.addOnScrollListener(scrollListener);

        // Query for existing tweets
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Showing data from database");
                List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();
                List<Tweet> tweetsFromDB = TweetWithUser.getTweetList(tweetWithUsers);
                adapter.clear();
                adapter.addAll(tweetsFromDB);
            }
        });

        populateTimeline();
    }

    private void loadNextDataFromApi(int page) {
        // Send an API request to retrieve appropriate paginated data
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                //  --> Deserialize and construct new model objects from the API response
                JSONArray jsonArray = json.jsonArray;
                try{
                    HomeActivity.showProgressBar();
                    List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);
                    adapter.addAll(tweets);
                }catch(JSONException e){
                    e.printStackTrace();
                }
                HomeActivity.hideProgressBar();
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure: " + throwable.getMessage());
            }
        }, tweets.get(tweets.size() - 1).id);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == COMPOSE_REQUEST && resultCode == RESULT_OK) {
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("NEW_TWEET"));
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
    }


    public void populateTimeline(){
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    HomeActivity.showProgressBar();
                    final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(json.jsonArray);
                    tweets.addAll(Tweet.fromJsonArray(json.jsonArray));
                    adapter.notifyDataSetChanged();
                    // Query for existing tweets
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Saving data to db");
                            // Insert users first
                            List<User> usersFromNetwork = User.fromTweetArray(tweetsFromNetwork);
                            tweetDao.insertModel(usersFromNetwork.toArray(new User[0]));
                            // Insert tweets
                            tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HomeActivity.hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Response: " + response + "\nThrowable: " + throwable.getMessage());
            }
        });
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    adapter.clear();
                    adapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "OnFailure: " + throwable.getMessage());
            }
        });
    }

    @Override
    public void onComposeTweet(final String tweetBody) {
        client.publishTweet(tweetBody, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    Tweet newTweet = Tweet.fromJsonObject(json.jsonObject);
                    tweets.add(0, newTweet);
                    adapter.notifyItemInserted(0);
                    rvTweets.smoothScrollToPosition(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "OnFailure: " + throwable.getMessage());
            }
        });
    }
}
