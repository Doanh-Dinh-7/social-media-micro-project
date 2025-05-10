package com.example.social_app.view.adapters;

import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social_app.R;
import com.example.social_app.model.CuocTroChuyen;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private List<CuocTroChuyen> conversationList;
    private OnConversationClickListener listener;

    public interface OnConversationClickListener {
        void onConversationClick(CuocTroChuyen conversation);
    }


    public ConversationAdapter(List<CuocTroChuyen> conversationList, OnConversationClickListener listener) {
        this.conversationList = conversationList;
        this.listener = listener;
    }

    public void updateList(List<CuocTroChuyen> newList) {
        this.conversationList = newList;
        notifyDataSetChanged();
    }


    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, int position) {
        CuocTroChuyen conversation = conversationList.get(position);
        holder.txtName.setText(conversation.getTen_nguoi_dung());
        holder.txtLastMessage.setText(conversation.getNoi_dung_cuoi());
        holder.txtTime.setText(conversation.getThoi_gian_cuoi());

        String avatarUrl = conversation.getAnh_dai_dien();

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(avatarUrl)
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.mipmap.user_img);
        }

        // Binding time for each item
        holder.bindTime(conversation.getThoi_gian_cuoi());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConversationClick(conversation); // Sửa ở đây, truyền toàn bộ đối tượng conversation
            }
        });
    }


    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtLastMessage, txtTime;
        private Handler handler = new Handler();
        private Runnable timeUpdater;
        private String originalTime;
        ImageView imgAvatar;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }

        public void bindTime(String ngayTao) {
            originalTime = ngayTao;
            updateTime(); // Call initially to set time

            if (timeUpdater != null) {
                handler.removeCallbacks(timeUpdater); // Clear previous handler if exists
            }

            timeUpdater = new Runnable() {
                @Override
                public void run() {
                    updateTime();
                    handler.postDelayed(this, 60000); // Update every minute
                }
            };
            handler.postDelayed(timeUpdater, 60000);
        }

        private void updateTime() {
            txtTime.setText(ConversationAdapter.getTimeAgo(originalTime)); // Correct reference to ConversationAdapter
        }
    }

    public static String getTimeAgo(String isoTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            Date commentDate = sdf.parse(isoTime);
            long now = System.currentTimeMillis();
            long time = commentDate.getTime();

            long diff = now - time;

            if (diff < DateUtils.MINUTE_IN_MILLIS) {
                return "Vừa xong";
            } else if (diff < DateUtils.HOUR_IN_MILLIS) {
                long minutes = diff / DateUtils.MINUTE_IN_MILLIS;
                return minutes + " phút trước";
            } else if (diff < DateUtils.DAY_IN_MILLIS) {
                long hours = diff / DateUtils.HOUR_IN_MILLIS;
                return hours + " giờ trước";
            } else if (diff < 2 * DateUtils.DAY_IN_MILLIS) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                return "Hôm qua lúc " + timeFormat.format(commentDate);
            } else {
                SimpleDateFormat fullFormat = new SimpleDateFormat("dd 'Tháng' MM", Locale.getDefault());
                return fullFormat.format(commentDate);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
