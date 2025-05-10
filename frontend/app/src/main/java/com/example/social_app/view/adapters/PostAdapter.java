package com.example.social_app.view.adapters;

import android.content.Context;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social_app.R;
import com.example.social_app.model.HinhAnh;
import com.example.social_app.model.PostResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<PostResponse> postList;
    private Context context;
    private OnPostLikeListener likeListener;

    private OnPostCommentListener commentListener;

    public PostAdapter(List<PostResponse> postList, Context context, OnPostLikeListener likeListener, OnPostCommentListener commentListener) {
        this.postList = postList;
        this.context = context;
        this.likeListener = likeListener;
        this.commentListener = commentListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostResponse post = postList.get(position);

        String timeAgo = getTimeAgo(post.getNgayTao());
        holder.txtThoiGian.setText(timeAgo);
        holder.bindTime(post.getNgayTao());
        String avatarUrl = post.getNguoi_dung().getAnhDaiDien();

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(context)
                    .load(avatarUrl)
                    .placeholder(R.mipmap.user_img)
                    .into(holder.user_image);
        } else {
            holder.user_image.setImageResource(R.mipmap.user_img);
        }


        int maRiengTu = post.getMaQuyenRiengTu();

        switch (maRiengTu) {
            case 1:
                holder.imgPrivacyIcon.setImageResource(R.mipmap.ic_public);
                break;
            case 2:
                holder.imgPrivacyIcon.setImageResource(R.mipmap.ic_friends);
                break;
            case 3:
                holder.imgPrivacyIcon.setImageResource(R.mipmap.ic_lock);
                break;
            default:
                holder.imgPrivacyIcon.setVisibility(View.GONE); // không hiển thị nếu không xác định
                break;
        }

        int maChuDe = post.getMaChuDe();  // Kiểu int
        String tenChuDe = "";

// Gán tên chủ đề theo mã
        switch (maChuDe) {
            case 1:
                tenChuDe = "Kinh nghiệm";
                break;
            case 2:
                tenChuDe = "Đời sống";
                break;
            case 3:
                tenChuDe = "Hỏi đáp";
                break;
            case 4:
                tenChuDe = "Khác";
                break;
            default:
                tenChuDe = "Chủ đề?";
                break;
        }

        Log.d("PostAdapter", "maChuDe = " + maChuDe + " => tenChuDe = " + tenChuDe);


        holder.txtContent.setText(post.getNoiDung());
        holder.txtChuDe.setText(tenChuDe);

        String userName = post.getNguoi_dung().getTenNguoiDung();  // Make sure this getter method exists

        // Set the user's name dynamically
        holder.txtTen.setText(userName);

        List<HinhAnh> hinh_anh = post.getHinh_anh();

        if (hinh_anh != null && !hinh_anh.isEmpty()) {
            holder.gridSelectedImages.setVisibility(View.VISIBLE);
            ImageUrlAdapter imageAdapter = new ImageUrlAdapter(context, hinh_anh);
            holder.gridSelectedImages.setAdapter(imageAdapter);
        } else {
            holder.gridSelectedImages.setVisibility(View.GONE);
        }

        holder.txtSoLuotTim.setText(String.valueOf(post.getSo_luot_thich()));

        boolean daThich = post.isDa_thich();
        holder.btnLike.setImageResource(daThich ? R.mipmap.redheart : R.mipmap.heart);

        holder.btnLike.setOnClickListener(v -> {
            likeListener.onLikeClicked(post, position);
        });

        holder.btnComment.setOnClickListener(v -> {
            commentListener.onCommentClicked(post, position);
        });

        holder.txtSoBinhLuan.setText(String.valueOf(post.getSo_binh_luan()));
    }

    public interface OnPostLikeListener {
        void onLikeClicked(PostResponse post, int position);
    }

    public interface OnPostCommentListener {
        void onCommentClicked(PostResponse post, int position);
    }


    @Override
    public int getItemCount() {
        return postList.size();
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
                return minutes + " phút";
            } else if (diff < DateUtils.DAY_IN_MILLIS) {
                long hours = diff / DateUtils.HOUR_IN_MILLIS;
                return hours + " giờ";
            } else if (diff < 2 * DateUtils.DAY_IN_MILLIS) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                return "Hôm qua lúc " + timeFormat.format(commentDate);
            } else {
                SimpleDateFormat fullFormat = new SimpleDateFormat("dd 'Tháng' MM 'lúc' HH:mm", Locale.getDefault());
                return fullFormat.format(commentDate);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView txtContent;
        TextView txtChuDe;
        GridView gridSelectedImages;
        ImageView btnLike;
        TextView txtSoLuotTim;
        TextView txtSoBinhLuan;
        ImageView btnComment;
        TextView txtTen;
        ImageView imgPrivacyIcon;
        TextView txtThoiGian;
        ImageView user_image;

        private final Handler handler = new Handler();
        private Runnable timeUpdater;
        private String originalTime;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtChuDe = itemView.findViewById(R.id.txtChuDe);
            gridSelectedImages = itemView.findViewById(R.id.gridSelectedImages);
            txtSoLuotTim = itemView.findViewById(R.id.txtSoLuotTim);
            txtSoBinhLuan = itemView.findViewById(R.id.txtSoBinhLuan);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            txtTen = itemView.findViewById(R.id.txtTen);
            imgPrivacyIcon = itemView.findViewById(R.id.imgPrivacyIcon);
            txtThoiGian = itemView.findViewById(R.id.txtThoiGian);
            user_image = itemView.findViewById(R.id.imgAvatar);
        }

        public void bindTime(String ngayTao) {
            originalTime = ngayTao;
            updateTime();

            if (timeUpdater != null) {
                handler.removeCallbacks(timeUpdater);
            }

            timeUpdater = new Runnable() {
                @Override
                public void run() {
                    updateTime();
                    handler.postDelayed(this, 60000);
                }
            };
            handler.postDelayed(timeUpdater, 60000);
        }

        private void updateTime() {
            txtThoiGian.setText(PostAdapter.getTimeAgo(originalTime));
        }
    }
}
