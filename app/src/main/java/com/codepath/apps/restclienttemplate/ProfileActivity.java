package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.apps.restclienttemplate.Adapters.UserAdapter;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "ProfileActivity";
    public String id_str;
    RecyclerView rvUsers;
    List<User> users;
    UserAdapter adapter;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        id_str = getIntent().getStringExtra("id_str");
        rvUsers = findViewById(R.id.rvUsers);
        client = new TwitterClient(this);
        rvUsers = findViewById(R.id.rvUsers);
        users = new ArrayList<>();
        adapter = new UserAdapter(this, users);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvUsers.setAdapter(adapter);

        populateFollowers();
    }



    private void populateFollowers() {
        client.getFollowers(id_str, new JsonHttpResponseHandler() {
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