package com.example.social_app.view.adapters;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.example.social_app.R;

import java.util.ArrayList;
public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Uri> imageUris;

    public ImageAdapter(Context context, ArrayList<Uri> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    @Override
    public int getCount() {
        return imageUris.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_image, parent, false);
        }

        ImageView imageView = view.findViewById(R.id.imgSelected);
        ImageButton btnRemove = view.findViewById(R.id.btnRemove);

        imageView.setImageURI(imageUris.get(position));

        btnRemove.setOnClickListener(v -> {
            imageUris.remove(position);
            notifyDataSetChanged(); // cập nhật lại giao diện
        });

        return view;
    }
}