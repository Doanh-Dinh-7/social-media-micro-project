package com.example.social_app.view.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social_app.R;
import com.example.social_app.model.FriendResponse;
import com.example.social_app.model.NguoiDung;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private List<FriendResponse> list;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FriendResponse friend);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public FriendListAdapter(List<FriendResponse> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendResponse friend = list.get(position);
        NguoiDung nguoiDung = friend.getBan();

        if (nguoiDung == null) return;

        holder.txtName.setText(nguoiDung.getTenNguoiDung());

        Glide.with(context)
                .load(nguoiDung.getAnhDaiDien())
                .placeholder(R.mipmap.user_img)
                .into(holder.imgAvatar);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(friend);
            }
        });

        holder.txtHuyKB.setOnClickListener(v -> {
            int friendId = nguoiDung.getMaNguoiDung();

            SharedPreferences sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
            String token = sharedPref.getString("auth_token", "");

            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            Call<Void> call = apiService.huyKetBan("Bearer " + token, friendId);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        int currentPos = holder.getAdapterPosition();
                        if (currentPos != RecyclerView.NO_POSITION) {
                            list.remove(currentPos);
                            notifyItemRemoved(currentPos);
                        }
                        Toast.makeText(context, "Đã hủy bạn bè", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Không thể hủy bạn", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtHuyKB;
        ImageView imgAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtHuyKB = itemView.findViewById(R.id.txtHuyKB);
        }
    }
}
