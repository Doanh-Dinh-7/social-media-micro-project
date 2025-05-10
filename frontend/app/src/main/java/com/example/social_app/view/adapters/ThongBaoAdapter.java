package com.example.social_app.view.adapters;

import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_app.R;
import com.example.social_app.model.ThongBao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class ThongBaoAdapter extends RecyclerView.Adapter<ThongBaoAdapter.ViewHolder> {

    private List<ThongBao> thongBaoList;

    public ThongBaoAdapter(List<ThongBao> thongBaoList) {
        this.thongBaoList = thongBaoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thong_bao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ThongBao thongBao = thongBaoList.get(position);

        if (thongBao != null) {

            holder.noiDung.setText(thongBao.getNoiDung() != null ? thongBao.getNoiDung() : "Không có nội dung");
            holder.imgAvatar.setImageResource(R.mipmap.user_img);
            holder.bindTime(thongBao.getThoiGian()); // gọi đúng chỗ này
        }
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
            if (ngayTao == null) {
                thoiGian.setText("");
                return;
            }

            originalTime = ngayTao;
            updateTime();

            if (timeUpdater != null) {
                handler.removeCallbacks(timeUpdater);
            }

            timeUpdater = new Runnable() {
                @Override
                public void run() {
                    updateTime();
                    handler.postDelayed(this, 60000); // cập nhật mỗi phút
                }
            };
            handler.postDelayed(timeUpdater, 60000);
        }

        private void updateTime() {
            thoiGian.setText(getTimeAgo(originalTime));
        }
    }

    public static String getTimeAgo(String isoTime) {
        Log.d("DEBUG_TIME", "Chuỗi gốc từ API: " + isoTime);
        try {
            // Cắt bớt phần microseconds nếu có
            if (isoTime.contains(".")) {
                int dotIndex = isoTime.indexOf(".");
                isoTime = isoTime.substring(0, dotIndex + 4); // giữ lại 3 chữ số sau dấu chấm
                Log.d("DEBUG_TIME", "Chuỗi sau khi cắt: " + isoTime);
            }

            // Parse chuỗi thành Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Thời gian từ API là UTC
            Date parsedDate = sdf.parse(isoTime);
            if (parsedDate == null) {
                Log.e("DEBUG_TIME", "parsedDate bị null");
                return "";
            }

            long timeInMillis = parsedDate.getTime();
            long now = System.currentTimeMillis();
            long diff = Math.max(0, now - timeInMillis);

            Log.d("DEBUG_TIME", "Thời gian parse (millis): " + timeInMillis);
            Log.d("DEBUG_TIME", "Hiện tại (millis): " + now);
            Log.d("DEBUG_TIME", "Chênh lệch (ms): " + diff);

            if (diff < DateUtils.MINUTE_IN_MILLIS) {
                return "Vừa xong";
            } else if (diff < DateUtils.HOUR_IN_MILLIS) {
                return (diff / DateUtils.MINUTE_IN_MILLIS) + " phút trước";
            } else if (diff < DateUtils.DAY_IN_MILLIS) {
                return (diff / DateUtils.HOUR_IN_MILLIS) + " giờ trước";
            } else if (diff < 2 * DateUtils.DAY_IN_MILLIS) {
                SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                return "Hôm qua lúc " + hourFormat.format(parsedDate);
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'Tháng' MM 'lúc' HH:mm", Locale.getDefault());
                return dateFormat.format(parsedDate);
            }

        } catch (ParseException e) {
            Log.e("DEBUG_TIME", "Lỗi parse thời gian: " + e.getMessage());
            return "";
        }
    }


}