package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class User {

    public User(){

    }

    public static User currentUser;

    public String handle;
    public String username;
    public String ivProfileUrl;
    public String description;
    public String id_str;

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
}
