package com.example.social_app.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social_app.R;
import com.example.social_app.model.NguoiDung;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<NguoiDung> userList;
    private Context context;

    public UserAdapter(List<NguoiDung> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        NguoiDung user = userList.get(position);
        holder.txtName.setText(user.getTenNguoiDung());

        Glide.with(context)
                .load(user.getAnhDaiDien())
                .placeholder(R.mipmap.user_img) // placeholder khi ảnh đang tải
                .into(holder.imgAvatar);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        ImageView imgAvatar;

        public UserViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}
