package com.example.social_app.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social_app.R;
import com.example.social_app.model.NguoiDung;

import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    private Context context;
    private List<NguoiDung> suggestionList;
    private OnActionListener listener;


    public interface OnActionListener {
        void onAccept(NguoiDung nguoiDung);
    }

    public SuggestionAdapter(Context context, List<NguoiDung> suggestionList, OnActionListener listener) {
        this.context = context;
        this.suggestionList = suggestionList;
        this.listener = listener;
    }

    public void updateUserStatus(NguoiDung user, int newStatus) {
        for (int i = 0; i < suggestionList.size(); i++) {
            NguoiDung nd = suggestionList.get(i);
            if (nd.getMaNguoiDung() == user.getMaNguoiDung()) {
                nd.setQuanHe(newStatus);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NguoiDung nguoiDung = suggestionList.get(position);
        holder.txtTen.setText(nguoiDung.getTenNguoiDung());

        Glide.with(context)
                .load(nguoiDung.getAnhDaiDien())
                .placeholder(R.mipmap.user_img)
                .into(holder.imgAvatar);

        Glide.with(context)
                .load(nguoiDung.getAnhBia())
                .placeholder(R.mipmap.anhbia)
                .into(holder.imgCover);

        int quanHe = nguoiDung.getQuanHe();
        if (quanHe == 2) {
            holder.btnKetBan.setText("Hủy lời mời");
        } else {
            holder.btnKetBan.setText("Kết bạn");
        }

        holder.btnKetBan.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAccept(nguoiDung);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTen;
        Button btnKetBan;
        ImageView imgAvatar;
        ImageView imgCover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTen = itemView.findViewById(R.id.txtTenNguoiDung);
            btnKetBan = itemView.findViewById(R.id.btnKetBan);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            imgCover = itemView.findViewById(R.id.imgCover);
        }
    }
}