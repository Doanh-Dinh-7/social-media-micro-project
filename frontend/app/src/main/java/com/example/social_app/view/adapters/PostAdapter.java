//package com.example.social_app.view.adapters;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.GridView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.social_app.R;
//import com.example.social_app.model.ChuDe;
//import com.example.social_app.model.HinhAnh;
//import com.example.social_app.model.PostResponse;
//
//import java.util.List;
//
//public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
//
//    private List<PostResponse> postList;
//    private List<ChuDe> chuDeList;
//    private Context context;
//
//    public PostAdapter(List<PostResponse> postList,  List<ChuDe> chuDeList, Context context) {
//        this.postList = postList;
//        this.chuDeList = chuDeList;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
//        return new PostViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
//        PostResponse post = postList.get(position);
//
//        Integer maChuDe = post.getMaChuDe();  // Lấy MaChuDe từ Post, kiểu Integer
//        String tenChuDe = "";
//
//        for (ChuDe cd : chuDeList) {
//            // So sánh trực tiếp giữa int và Integer, Java sẽ tự động unbox
//            if (cd.getMaChuDe() == maChuDe) {
//                tenChuDe = cd.getLoai();  // Lấy Loại của Chủ đề
//                break;
//            }
//        }
//
//
//        holder.txtContent.setText(post.getNoiDung());
//        holder.txtChuDe.setText(tenChuDe);
//
//        List<HinhAnh> hinh_anh = post.getHinh_anh(); // hoặc post.getAnh(), tùy theo bạn đặt
//
//        if (hinh_anh != null && !hinh_anh.isEmpty()) {
//            holder.gridSelectedImages.setVisibility(View.VISIBLE);
//            ImageUrlAdapter imageAdapter = new ImageUrlAdapter(context, hinh_anh);
//            holder.gridSelectedImages.setAdapter(imageAdapter);
//        } else {
//            holder.gridSelectedImages.setVisibility(View.GONE);
//        }
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return postList.size();
//    }
//
//    public static class PostViewHolder extends RecyclerView.ViewHolder {
//        TextView txtContent;
//        TextView txtChuDe;
//
//        GridView gridSelectedImages;
//
//        public PostViewHolder(@NonNull View itemView) {
//            super(itemView);
//            txtContent = itemView.findViewById(R.id.txtContent);
//            txtChuDe = itemView.findViewById(R.id.txtChuDe);
//            gridSelectedImages = itemView.findViewById(R.id.gridSelectedImages);
//        }
//
//    }
//}
//
package com.example.social_app.view.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_app.R;
import com.example.social_app.model.ChuDe;
import com.example.social_app.model.HinhAnh;
import com.example.social_app.model.PostResponse;

import java.util.List;

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
            default:
                tenChuDe = "Chủ đề?";
                break;
        }

        Log.d("PostAdapter", "maChuDe = " + maChuDe + " => tenChuDe = " + tenChuDe);

        holder.txtContent.setText(post.getNoiDung());
        holder.txtChuDe.setText(tenChuDe);



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

        holder.txtSoLuotBinhLuan.setText(String.valueOf(post.getSo_binh_luan()));
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

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView txtContent;
        TextView txtChuDe;
        GridView gridSelectedImages;
        ImageView btnLike;
        TextView txtSoLuotTim;
        TextView txtSoLuotBinhLuan;
        ImageView btnComment;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtChuDe = itemView.findViewById(R.id.txtChuDe);
            gridSelectedImages = itemView.findViewById(R.id.gridSelectedImages);
            txtSoLuotTim = itemView.findViewById(R.id.txtSoLuotTim);
            txtSoLuotBinhLuan = itemView.findViewById(R.id.txtSoLuotBinhLuan);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
        }
    }
}
