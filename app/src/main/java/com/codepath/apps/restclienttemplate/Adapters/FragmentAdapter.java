package com.codepath.apps.restclienttemplate.Adapters;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.codepath.apps.restclienttemplate.fragments.FollowersFragment;
import com.codepath.apps.restclienttemplate.fragments.FollowingFragment;
import com.codepath.apps.restclienttemplate.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FragmentAdapter extends FragmentStateAdapter {
    public static final String TAG = "FragmentAdapter";
    Context context;
    List<User> users;

    public FragmentAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle,
                           Context context, List<User> users) {
        super(fragmentManager, lifecycle);
        this.users = users;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        Log.d(TAG, "Position: " + position);
        switch (position){
            case 1:
                return new FollowersFragment(context, users);
            default:
                return new FollowingFragment(context, users);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
