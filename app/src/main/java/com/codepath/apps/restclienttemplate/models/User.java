package com.codepath.apps.restclienttemplate.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Entity
@Parcel
public class User {

    public User(){

    }

    @ColumnInfo
    @PrimaryKey
    @NonNull
    public String id_str;

    @ColumnInfo
    public static User currentUser;

    @ColumnInfo
    public String handle;
    @ColumnInfo
    public String username;
    @ColumnInfo
    public String ivProfileUrl;
    @ColumnInfo
    public String description;

    public static User fromJsonObject(JSONObject object) throws JSONException {
        User user = new User();
        user.description = null;
        user.handle = object.getString("screen_name");
        user.username = object.getString("name");
        user.ivProfileUrl = object.getString("profile_image_url_https");
        user.id_str = object.getString("id_str");

        if(!(object.isNull("description"))) {
            user.description = object.getString("description");
        }
        return user;
    }

    public static List<User> fromJsonArray(JSONArray array) throws JSONException{
        List<User> users = new ArrayList<>();
        for(int i = 0; i < array.length(); i++){
            users.add(fromJsonObject(array.getJSONObject(i)));
        }
        return users;
    }

    public static List<User> fromTweetArray(List<Tweet> tweetsFromNetwork) {
        List<User> users = new ArrayList<>();

        for(int i = 0; i < tweetsFromNetwork.size(); i++){
            users.add(tweetsFromNetwork.get(i).user);
        }

        return users;
    }
}
