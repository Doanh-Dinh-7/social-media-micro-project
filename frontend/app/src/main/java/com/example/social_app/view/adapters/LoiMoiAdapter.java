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
import com.example.social_app.model.LoiMoiKetBan;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LoiMoiAdapter extends RecyclerView.Adapter<LoiMoiAdapter.LoiMoiViewHolder> {

    private Context context;
    private List<LoiMoiKetBan> loiMoiList;
    private OnActionListener listener;

    public interface OnActionListener {
        void onAccept(int maLoiMoi);
        void onDelete(int maLoiMoi);
    }

    public LoiMoiAdapter(Context context, List<LoiMoiKetBan> loiMoiList, OnActionListener listener) {
        this.context = context;
        this.loiMoiList = loiMoiList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LoiMoiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_loi_moi, parent, false);
        return new LoiMoiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoiMoiViewHolder holder, int position) {
        LoiMoiKetBan loiMoi = loiMoiList.get(position);

        Glide.with(context)
                .load(loiMoi.getNguoi_gui().getAnhDaiDien())
                .placeholder(R.mipmap.user_img)
                .into(holder.avatarImageView);

        // Gán tên người gửi (giả sử LoiMoiKetBan có getNguoiGui().getTenNguoiDung())
        holder.nameTextView.setText(loiMoi.getNguoi_gui().getTenNguoiDung());

        // Gán thời gian gửi
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
            Date sendTime = sdf.parse(loiMoi.getThoiGian());
            long diff = new Date().getTime() - sendTime.getTime();

            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            long days = TimeUnit.MILLISECONDS.toDays(diff);

            if (minutes < 60) {
                holder.timeTextView.setText(minutes + " phút");
            } else if (hours < 24) {
                holder.timeTextView.setText(hours + " giờ");
            } else {
                holder.timeTextView.setText(days + " ngày");
            }
        } catch (Exception e) {
            holder.timeTextView.setText("Không rõ thời gian");
        }



        // Xử lý sự kiện nút
        holder.acceptButton.setOnClickListener(v -> listener.onAccept(loiMoi.getMaLoiMoi()));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(loiMoi.getMaLoiMoi()));
    }

    @Override
    public int getItemCount() {
        return loiMoiList.size();
    }

    public static class LoiMoiViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, timeTextView;
        MaterialButton acceptButton, deleteButton;
        ImageView avatarImageView;

        public LoiMoiViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
        }
    }
}
