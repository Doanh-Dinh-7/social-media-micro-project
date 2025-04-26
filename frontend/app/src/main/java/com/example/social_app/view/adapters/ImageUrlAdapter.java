package com.example.social_app.view.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.social_app.R;
import com.example.social_app.model.HinhAnh;

import java.util.List;

public class ImageUrlAdapter extends BaseAdapter {
    private Context context;
    private List<HinhAnh> imageUrls;

    public ImageUrlAdapter(Context context, List<HinhAnh> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int i) {
        return imageUrls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(300, 300)); // size ảnh
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        // Lấy URL từ object HinhAnh
        String imageUrl = imageUrls.get(position).getUrl();

        Glide.with(context)
                .load(imageUrl)
                .into(imageView);

        return imageView;

    }
}
