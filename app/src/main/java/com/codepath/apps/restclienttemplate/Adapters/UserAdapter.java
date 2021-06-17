package com.codepath.apps.restclienttemplate.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    List<User> users;
    Context context;

    public UserAdapter(Context context, List<User> users){
        this.users = users;
        this.context = context;
    }
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void clear(){
        users.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<User> users){
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        TextView tvHandle;
        TextView tvDescription;
        ImageView ivProfile;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvName = itemView.findViewById(R.id.tvUsername);
            tvHandle = itemView.findViewById(R.id.tvHandle);
            ivProfile = itemView.findViewById(R.id.rvUser);
        }

        public void bind(User user) {
            tvName.setText(user.username);
            tvHandle.setText("@" + user.handle);
            if(user.description != null) {
                tvDescription.setText(user.description);
            }
            else{
                tvDescription.setVisibility(View.GONE);
            }

            Glide.with(context)
                    .load(user.ivProfileUrl)
                    .fitCenter()
                    .transform(new CircleCrop())
                    .override(250, 130)
                    .into(ivProfile);
        }
    }
}
