package com.example.social_app.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social_app.R;
import com.example.social_app.model.HinhAnh;

import java.util.List;

public class ImageProfileAdapter extends RecyclerView.Adapter<ImageProfileAdapter.ImageProfileViewHolder> {

    private List<HinhAnh> imageUrls;
    private Context context;

    public ImageProfileAdapter(Context context, List<HinhAnh> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    public static class ImageProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageProfileViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }

    @Override
    public ImageProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_profile, parent, false);
        return new ImageProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageProfileViewHolder holder, int position) {
        HinhAnh hinhAnh = imageUrls.get(position);
        String url = hinhAnh.getUrl();
        Glide.with(context).load(url).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }
}
