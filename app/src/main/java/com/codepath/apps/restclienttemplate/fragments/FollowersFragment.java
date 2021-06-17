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

public class FollowersFragment extends Fragment {

    public static final String TAG = "FollowersFragment";
    Context context;
    RecyclerView rvUsers;
    TwitterClient client;
    List<User> users;
    UserAdapter adapter;

    public FollowersFragment(Context context, List<User> users){
        this.context = context;
        this.users = users;
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
        rvUsers.setLayoutManager(new LinearLayoutManager(context));
        rvUsers.setAdapter(adapter);
        rvUsers.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        getFollowers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_followers, container, false);
    }

    private void getFollowers() {
        adapter.clear();
        users = new ArrayList<>();
        client.getFollowers(ProfileActivity.getID(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray array = null;
                try {
                    array = json.jsonObject.getJSONArray("users");
                    users.addAll(User.fromJsonArray(array));
                    adapter.addAll(users);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "OnSuccess");
                } catch (JSONException e) {
                    Log.d(TAG, "Exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "OnFailure: " + throwable.getMessage());
            }
        });
    }
}