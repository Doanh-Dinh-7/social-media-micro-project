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
import com.example.social_app.model.ThongBao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ThongBaoAdapter extends RecyclerView.Adapter<ThongBaoAdapter.ViewHolder> {

    private List<ThongBao> thongBaoList;
    private Context context;

    public ThongBaoAdapter(List<ThongBao> thongBaoList) {
        this.thongBaoList = thongBaoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thong_bao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ThongBao thongBao = thongBaoList.get(position);

        if (thongBao != null) {

            holder.noiDung.setText(thongBao.getNoiDung() != null ? thongBao.getNoiDung() : "Không có nội dung");

            String avatarUrl = thongBao.getAnhDaiDien();

            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(context)
                        .load(avatarUrl)
                        .placeholder(R.mipmap.user_img)
                        .into(holder.imgAvatar);
            } else {
                holder.imgAvatar.setImageResource(R.mipmap.user_img);
            }



            holder.bindTime(thongBao.getThoiGian());
        }
    }

    @Override
    public void onViewRecycled(@NonNull ThongBaoAdapter.ViewHolder holder) {
        holder.unbind();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return thongBaoList != null ? thongBaoList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView noiDung, thoiGian;
        private final Handler handler = new Handler();
        private Runnable timeUpdater;
        private String originalTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            noiDung = itemView.findViewById(R.id.noiDung);
            thoiGian = itemView.findViewById(R.id.thoiGian);
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
            thoiGian.setText(ThongBaoAdapter.getTimeAgo(originalTime));
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


}