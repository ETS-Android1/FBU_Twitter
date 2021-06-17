package com.codepath.apps.restclienttemplate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.codepath.apps.restclienttemplate.Adapters.FragmentAdapter;
import com.codepath.apps.restclienttemplate.models.User;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "ProfileActivity";

    static String id_str;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter fragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager2);

        id_str = getIntent().getStringExtra("id_str");

        FragmentManager fm = getSupportFragmentManager();
        List<User> users = new ArrayList<>();
        fragmentAdapter = new FragmentAdapter(fm, getLifecycle(), this, users);
        viewPager2.setAdapter(fragmentAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("Following"));
        tabLayout.addTab(tabLayout.newTab().setText("Followers"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    public static String getID(){
        return id_str;
    }
}