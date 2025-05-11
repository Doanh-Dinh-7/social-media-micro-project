package com.example.social_app.view.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social_app.R;
import com.example.social_app.model.NguoiDung;

import java.util.List;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.UserViewHolder> {

    private Context context;
    private List<NguoiDung> userList;
    private int currentUserId;
    private FriendRequestListener listener;

    public UserSearchAdapter(Context context, List<NguoiDung> userList, int currentUserId, FriendRequestListener listener) {
        this.context = context;
        this.userList = userList;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_search, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        NguoiDung user = userList.get(position);
        if (user == null) return;

        holder.txtTenNguoiDung.setText(user.getTenNguoiDung());

        Glide.with(context)
                .load(user.getAnhDaiDien())
                .placeholder(R.mipmap.user_img)
                .into(holder.imgAvatar);

        switch (user.getQuanHe()) {
            case 1:
                holder.btnAction.setVisibility(View.GONE);
                holder.txtTrangThai.setVisibility(View.VISIBLE);
                holder.txtTrangThai.setText("Bạn bè");
                break;

            case 2:
                holder.btnAction.setVisibility(View.VISIBLE);
                holder.txtTrangThai.setVisibility(View.GONE);
                holder.btnAction.setText("Hủy lời mời");
                holder.btnAction.setEnabled(true);
                holder.btnAction.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F9683B")));
                holder.btnAction.setTextColor(Color.WHITE);
                holder.btnAction.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onCancelFriendRequest(user);
                    }
                });
                break;

            case 3:
                holder.btnAction.setVisibility(View.VISIBLE);
                holder.txtTrangThai.setVisibility(View.GONE);
                holder.btnAction.setText("Chờ phản hồi");
                holder.btnAction.setEnabled(false);
                holder.btnAction.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                holder.btnAction.setTextColor(Color.DKGRAY);
                break;

            case 4:
                holder.btnAction.setVisibility(View.VISIBLE);
                holder.txtTrangThai.setVisibility(View.GONE);
                holder.btnAction.setText("Kết bạn");
                holder.btnAction.setEnabled(true);
                holder.btnAction.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.orange)));
                holder.btnAction.setTextColor(Color.WHITE);
                holder.btnAction.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onSendFriendRequest(user);
                    }
                });
                break;
        }
    }

    public void updateUserStatus(NguoiDung user, int newStatus) {
        int position = -1;
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getMaNguoiDung() == user.getMaNguoiDung()) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            userList.get(position).setQuanHe(newStatus);
            notifyItemChanged(position);
        }
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface FriendRequestListener {
        void onSendFriendRequest(NguoiDung nguoiDung);
        void onCancelFriendRequest(NguoiDung nguoiDung);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtTenNguoiDung;
        Button btnAction;
        TextView txtTrangThai;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtTenNguoiDung = itemView.findViewById(R.id.txtTenNguoiDung);
            btnAction = itemView.findViewById(R.id.btnAction);
            txtTrangThai = itemView.findViewById(R.id.txtTrangThai);
        }
    }
}




