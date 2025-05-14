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
import com.example.social_app.model.CommentResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context context;
    private List<CommentResponse> commentList;
    private int currentUserId;
    private OnCommentActionListener listener;

    public interface OnCommentActionListener {
        void onDeleteComment(CommentResponse comment);
        void onCommentCountChanged(int count);
    }

    public CommentAdapter(Context context, List<CommentResponse> commentList, int currentUserId, OnCommentActionListener listener) {
        this.context = context;
        this.commentList = commentList;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentResponse comment = commentList.get(position);

        holder.txtUserName.setText(comment.getTenNguoiDung());
        holder.txtCommentContent.setText(comment.getNoiDung());
        holder.bindTime(comment.getNgayTao());

        Glide.with(context)
                .load(comment.getAnhDaiDien())
                .placeholder(R.mipmap.user_img)
                .into(holder.imgAvatar);

        holder.itemView.setOnLongClickListener(v -> {
            if (comment.getMaNguoiDung() == currentUserId) {
                showBottomSheet(holder.itemView.getContext(), comment);
            }
            return true;
        });
    }

    @Override
    public void onViewRecycled(@NonNull CommentViewHolder holder) {
        holder.unbind();
        super.onViewRecycled(holder);
    }

    private void showBottomSheet(Context context, CommentResponse comment) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_comment_actions, null);

        TextView btnDelete = view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> {
            listener.onDeleteComment(comment);
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName, txtCommentContent, txtCommentTime;
        ImageView imgAvatar;
        private Handler handler = new Handler();
        private Runnable timeUpdater;
        private String originalTime;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtCommentContent = itemView.findViewById(R.id.txtCommentContent);
            txtCommentTime = itemView.findViewById(R.id.txtCommentTime);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
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
            txtCommentTime.setText(CommentAdapter.getTimeAgo(originalTime));
        }
    }

    public void setCommentList(List<CommentResponse> newCommentList) {
        this.commentList = newCommentList;
        notifyDataSetChanged();
        if (listener != null) {
            listener.onCommentCountChanged(getItemCount());
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
