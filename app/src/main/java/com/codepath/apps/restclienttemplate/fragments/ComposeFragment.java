package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.NewTweetListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.User;

public class ComposeFragment extends DialogFragment implements NewTweetListener {

    public static final String TAG = "ComposeFragment";
    public static final int MAX_TWEET_LENGTH = 140;
    EditText etCompose;
    Button btTweet;
    Button btCancel;
    ImageView ivProfile;

    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {

        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }

    @Override
    public void onComposeTweet(String tweetBody) {
        NewTweetListener listener = (NewTweetListener) getTargetFragment();
        listener.onComposeTweet(tweetBody);
        dismiss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etCompose = view.findViewById(R.id.etCompose);
        btTweet = view.findViewById(R.id.btTweet);
        btCancel = view.findViewById(R.id.btCancel);
        ivProfile = view.findViewById(R.id.rvUser);
        etCompose.requestFocus();

        Glide.with(this)
                .load(User.currentUser.ivProfileUrl)
                .circleCrop()
                .into(ivProfile);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etCompose.getText())){
                    etCompose.setError("Cannot be empty!");
                    return;
                }
                if(etCompose.getText().length() > MAX_TWEET_LENGTH){
                    etCompose.setError("Tweet is too long!");
                    return;
                }

                onComposeTweet(etCompose.getText().toString());
                dismiss();
            }
        });
    }
}