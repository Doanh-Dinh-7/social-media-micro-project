package com.example.social_app.view.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social_app.R;
import com.example.social_app.model.NguoiDung;
import com.example.social_app.model.LoiMoiKetBan;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<NguoiDung> userList;
    private Context context;
    private boolean showFriendButton;
    private OnItemClickListener listener;
    private List<LoiMoiKetBan> friendRequests;
    private int currentUserId;

    public UserAdapter(List<NguoiDung> userList, boolean showFriendButton, Context context,
                       OnItemClickListener listener, List<LoiMoiKetBan> friendRequests, int currentUserId) {
        this.userList = userList;
        this.context = context;
        this.showFriendButton = showFriendButton;
        this.listener = listener;
        this.friendRequests = friendRequests;
        this.currentUserId = currentUserId;
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
                .placeholder(R.mipmap.user_img)
                .into(holder.imgAvatar);

        LoiMoiKetBan request = getFriendRequest(user.getMaNguoiDung());

        if (showFriendButton) {
            if (request != null) {
                int trangThai = request.getTrangThai();

                if (trangThai == 1) {
                    // Là bạn bè
                    holder.btnFriendRequest.setVisibility(View.GONE);
                    holder.txtFriendStatus.setVisibility(View.VISIBLE);
                    holder.txtFriendStatus.setText("Bạn bè");
                } else if (trangThai == 0) {
                    // Đã gửi lời mời
                    holder.txtFriendStatus.setVisibility(View.GONE);
                    holder.btnFriendRequest.setVisibility(View.VISIBLE);
                    holder.btnFriendRequest.setText("Hủy lời mời");
                    holder.btnFriendRequest.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue_600));
                } else if (trangThai == 2) {
                    // Đã từ chối hoặc hủy, cho gửi lại lời mời
                    holder.txtFriendStatus.setVisibility(View.GONE);
                    holder.btnFriendRequest.setVisibility(View.VISIBLE);
                    holder.btnFriendRequest.setText("Kết bạn");
                    holder.btnFriendRequest.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
                }
            } else {
                // Chưa có mối quan hệ
                holder.txtFriendStatus.setVisibility(View.GONE);
                holder.btnFriendRequest.setVisibility(View.VISIBLE);
                holder.btnFriendRequest.setText("Kết bạn");
                holder.btnFriendRequest.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
            }
        } else {
            holder.txtFriendStatus.setVisibility(View.GONE);
            holder.btnFriendRequest.setVisibility(View.GONE);
        }


        holder.btnFriendRequest.setOnClickListener(v -> {
            if (listener != null) {
                if (request == null || request.getTrangThai() == 2) {
                    // Gửi kết bạn mới
                    listener.onSendFriendRequest(user);
                    LoiMoiKetBan newRequest = new LoiMoiKetBan();
                    newRequest.setNguoiGui(currentUserId);
                    newRequest.setNguoiNhan(user.getMaNguoiDung());
                    newRequest.setTrangThai(0);
                    newRequest.setThoiGian("");

                    friendRequests.add(newRequest);
                    notifyItemChanged(position);
                } else if (request.getTrangThai() == 0) {
                    // Hủy lời mời kết bạn
                    listener.onCancelFriendRequest(user);
                    friendRequests.remove(request);
                    notifyItemChanged(position);
                }
            }
        });
    }

    private LoiMoiKetBan getFriendRequest(int otherUserId) {
        if (friendRequests == null) {
            return null;
        }

        for (LoiMoiKetBan request : friendRequests) {
            if ((request.getNguoiGui() == currentUserId && request.getNguoiNhan() == otherUserId) ||
                    (request.getNguoiNhan() == currentUserId && request.getNguoiGui() == otherUserId)) {
                return request;
            }
        }
        return null;
    }

    public void updateFriendRequests(List<LoiMoiKetBan> newFriendRequests) {
        this.friendRequests = newFriendRequests;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtFriendStatus;
        ImageView imgAvatar;
        Button btnFriendRequest;

        public UserViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            btnFriendRequest = itemView.findViewById(R.id.btnFriendRequest);
            txtFriendStatus = itemView.findViewById(R.id.txtFriendStatus);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }

    public interface OnItemClickListener {
        void onSendFriendRequest(NguoiDung user);
        void onCancelFriendRequest(NguoiDung user);
        void onOpenProfile(NguoiDung user);
    }
}
