package com.example.social_app.view.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_app.R;
import com.example.social_app.model.CommentResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<CommentResponse> commentList;
    private int currentUserId;
    private OnCommentActionListener listener;

    public interface OnCommentActionListener {
        void onDeleteComment(CommentResponse comment);
        void onCommentCountChanged(int count);
    }

    public CommentAdapter(List<CommentResponse> commentList, int currentUserId, OnCommentActionListener listener) {
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
        holder.txtCommentTime.setText(comment.getNgayTao());

        Log.d("CommentAdapter", "Current User ID: " + currentUserId);
        Log.d("CommentAdapter", "Comment Author ID: " + comment.getMaNguoiDung());


        // Gắn long click cho từng item
        holder.itemView.setOnLongClickListener(v -> {
            if (comment.getMaNguoiDung() == currentUserId) {
                showBottomSheet(holder.itemView.getContext(), comment);
            }
            return true;
        });
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

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtCommentContent = itemView.findViewById(R.id.txtCommentContent);
            txtCommentTime = itemView.findViewById(R.id.txtCommentTime);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }

    public void setCommentList(List<CommentResponse> newCommentList) {
        this.commentList = newCommentList;
        notifyDataSetChanged();
        if (listener != null) {
            listener.onCommentCountChanged(getItemCount());
        }
    }
}
