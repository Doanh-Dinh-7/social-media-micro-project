package com.example.social_app.view.adapters;

import android.content.Context;
import android.os.Handler;
import android.text.format.DateUtils;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        holder.nameTextView.setText(loiMoi.getNguoi_gui().getTenNguoiDung());
        holder.bindTime(loiMoi.getThoiGian());

        holder.acceptButton.setOnClickListener(v -> listener.onAccept(loiMoi.getMaLoiMoi()));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(loiMoi.getMaLoiMoi()));
    }

    @Override
    public int getItemCount() {
        return loiMoiList.size();
    }

    @Override
    public void onViewRecycled(@NonNull LoiMoiAdapter.LoiMoiViewHolder holder) {
        holder.unbind();
        super.onViewRecycled(holder);
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
                return (diff / DateUtils.MINUTE_IN_MILLIS) + " phút trước";
            } else if (diff < DateUtils.DAY_IN_MILLIS) {
                return (diff / DateUtils.HOUR_IN_MILLIS) + " giờ trước";
            } else if (diff < 2 * DateUtils.DAY_IN_MILLIS) {
                return "Hôm qua lúc " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(commentDate);
            } else {
                return new SimpleDateFormat("dd 'Tháng' MM 'lúc' HH:mm", Locale.getDefault()).format(commentDate);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static class LoiMoiViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, timeTextView;
        MaterialButton acceptButton, deleteButton;
        ImageView avatarImageView;
        private Handler handler = new Handler();
        private Runnable timeUpdater;
        private String originalTime;

        public LoiMoiViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
        }

        public void bindTime(String ngayTao) {
            originalTime = ngayTao;
            updateTime();

            if (timeUpdater != null) handler.removeCallbacks(timeUpdater);

            timeUpdater = () -> {
                updateTime();
                handler.postDelayed(timeUpdater, 60000);
            };
            handler.postDelayed(timeUpdater, 60000);
        }

        public void unbind() {
            if (timeUpdater != null) {
                handler.removeCallbacks(timeUpdater);
                timeUpdater = null;
            }
        }

        private void updateTime() {
            timeTextView.setText(LoiMoiAdapter.getTimeAgo(originalTime));
        }
    }
    public void removeLoiMoiById(int maLoiMoi) {
        for (int i = 0; i < loiMoiList.size(); i++) {
            if (loiMoiList.get(i).getMaLoiMoi() == maLoiMoi) {
                loiMoiList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

}