package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.apps.restclienttemplate.Adapters.UserAdapter;
import com.codepath.apps.restclienttemplate.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.ProfileActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class FollowingFragment extends Fragment {

    public static final String TAG = "FollowingFragment";
    private EndlessRecyclerViewScrollListener scrollListener;
    Context context;
    RecyclerView rvUsers;
    TwitterClient client;
    List<User> users = new ArrayList<>();
    UserAdapter adapter;
    long cursorId = -1;
    int mode = 0; // 0 == "followers", 1 == "following"

    public FollowingFragment(Context context, List<User> users, int mode) {
        this.context = context;
        this.users = users;
        this.mode = mode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        rvUsers = view.findViewById(R.id.rvUsers);
        client = TwitterApp.getRestClient(context);
        adapter = new UserAdapter(context, users);
        rvUsers = view.findViewById(R.id.rvUsers);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        rvUsers.setLayoutManager(llm);
        rvUsers.setAdapter(adapter);
        rvUsers.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(mode == 0) {
                    getMoreFollowers();
                } else if (mode == 1) {
                    getMoreFollowing();
                }
            }
        };

        rvUsers.addOnScrollListener(scrollListener);

        users.clear();
        adapter.notifyDataSetChanged();
        if(mode == 0) {
            getMoreFollowers();
        } else if (mode == 1) {
            getMoreFollowing();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_following, container, false);
    }

    public void getMoreFollowers(){
        client.getFollowers(cursorId, ProfileActivity.getID(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray array = null;
                try {
                    cursorId = json.jsonObject.getLong("next_cursor");
                    array = json.jsonObject.getJSONArray("users");
                    users.addAll(User.fromJsonArray(array));
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "OnSuccess");
                } catch (JSONException e) {
                    Log.d(TAG, "Exception: " + e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
    }

    public void getMoreFollowing(){
        client.getFriends(cursorId, ProfileActivity.getID(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray array = null;
                try {
                    cursorId = json.jsonObject.getLong("next_cursor");
                    array = json.jsonObject.getJSONArray("users");
                    users.addAll(User.fromJsonArray(array));
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "OnSuccess");
                } catch (JSONException e) {
                    Log.d(TAG, "Exception: " + e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
    }
}