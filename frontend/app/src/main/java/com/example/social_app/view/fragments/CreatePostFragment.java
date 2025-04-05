package com.example.social_app.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.social_app.R;
import com.example.social_app.model.PostRequest;
import com.example.social_app.model.PostResponse;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.activities.PostActivity;
import com.example.social_app.view.adapters.ImageAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostFragment extends Fragment {
    private ImageButton btnClose;
    private Button btnPost;
    private EditText edtPostContent;
    private CardView btnTopic, btnObject;
    private RadioButton rbPublic, rbFriends, rbOnlyMe;
    private CheckBox checkboxDefault;
    private Button btnApply;
    private String selectedTopic = "";
    private String selectedObject = "";

    private ImageView imgPicture;
    private GridView gridSelectedImages;
    private ArrayList<Uri> selectedImageUris = new ArrayList<>();
    private ImageAdapter imageAdapter;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);

        initViews(view);
        setupListeners();
        setupGalleryLauncher();
        updateButtonState();

        return view;
    }

    private void initViews(View view) {
        btnClose = view.findViewById(R.id.btnClose);
        btnPost = view.findViewById(R.id.btnPost);
        edtPostContent = view.findViewById(R.id.edtPostContent);
        btnTopic = view.findViewById(R.id.btnTopic);
        btnObject = view.findViewById(R.id.btnObject);
        imgPicture = view.findViewById(R.id.imgPicture);
        gridSelectedImages = view.findViewById(R.id.gridSelectedImages);
        imageAdapter = new ImageAdapter(getContext(), selectedImageUris);
        gridSelectedImages.setAdapter(imageAdapter);
    }

    private void setupListeners() {
        btnClose.setOnClickListener(v -> ((PostActivity) getActivity()).onBack());
        btnTopic.setOnClickListener(v -> showTopicBottomSheet());
        btnObject.setOnClickListener(v -> showObjectBottomSheet());
        imgPicture.setOnClickListener(v -> openGallery());
        btnPost.setOnClickListener(v -> createPost());

        edtPostContent.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                updateButtonState();
            }
        });

        gridSelectedImages.setOnItemLongClickListener((parent, view, position, id) -> {
            selectedImageUris.remove(position);
            imageAdapter.notifyDataSetChanged();
            if (selectedImageUris.isEmpty()) {
                gridSelectedImages.setVisibility(View.GONE);
            }
            return true;
        });
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri uri = data.getClipData().getItemAt(i).getUri();
//                                selectedImageUris.add(Uri.parse("content://fake/test.jpg"));
                                selectedImageUris.add(uri);
                            }
                        } else if (data.getData() != null) {
                            Uri uri = data.getData();
                            selectedImageUris.add(uri);
                        }

                        if (!selectedImageUris.isEmpty()|| imageAdapter.getCount() > 0) {
                            gridSelectedImages.setVisibility(View.VISIBLE);
                            imageAdapter.notifyDataSetChanged();
                        }
                    }
                }
        );
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryLauncher.launch(intent);
    }

    private void updateButtonState() {
        boolean hasContent = !edtPostContent.getText().toString().trim().isEmpty();
        boolean hasTopic = !selectedTopic.isEmpty();
        boolean hasObject = !selectedObject.isEmpty();
        btnPost.setEnabled(hasContent && hasTopic && hasObject);
    }
    public void updateApplyButtonState() {
        boolean isAnyRadioChecked = rbPublic.isChecked() || rbFriends.isChecked() || rbOnlyMe.isChecked();
        boolean isCheckboxChecked = checkboxDefault.isChecked();

        btnApply.setEnabled(isAnyRadioChecked && isCheckboxChecked);
    }


    private void showTopicBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_topic, null);
        bottomSheetDialog.setContentView(sheetView);

        TextView txtTopic = (TextView) btnTopic.getChildAt(0); // lấy TextView bên trong CardView

        sheetView.findViewById(R.id.optionPublic).setOnClickListener(v -> {
            selectedTopic = "Kinh nghiệm";
            txtTopic.setText(selectedTopic + " ▼");
            bottomSheetDialog.dismiss();
            updateButtonState();
        });

        sheetView.findViewById(R.id.optionLife).setOnClickListener(v -> {
            selectedTopic = "Đời sống";
            txtTopic.setText(selectedTopic + " ▼");
            bottomSheetDialog.dismiss();
            updateButtonState();
        });

        bottomSheetDialog.show();
    }
    private void showObjectBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_object, null);
        bottomSheetDialog.setContentView(sheetView);

        rbPublic = sheetView.findViewById(R.id.rbPublic);
        rbFriends = sheetView.findViewById(R.id.rbFriends);
        rbOnlyMe = sheetView.findViewById(R.id.rbOnlyMe);
        checkboxDefault = sheetView.findViewById(R.id.checkboxDefault);
        btnApply = sheetView.findViewById(R.id.btn_apply);

        btnApply.setEnabled(false);

        rbPublic.setOnClickListener(v -> {
            rbFriends.setChecked(false);
            rbOnlyMe.setChecked(false);
            updateApplyButtonState();
        });

        rbFriends.setOnClickListener(v -> {
            rbPublic.setChecked(false);
            rbOnlyMe.setChecked(false);
            updateApplyButtonState();
        });

        rbOnlyMe.setOnClickListener(v -> {
            rbPublic.setChecked(false);
            rbFriends.setChecked(false);
            updateApplyButtonState();
        });

        checkboxDefault.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateApplyButtonState();
        });

        btnApply.setOnClickListener(v -> {
            String selected = "";
            if (rbPublic.isChecked()) selected = "Mọi người";
            else if (rbFriends.isChecked()) selected = "Bạn bè";
            else if (rbOnlyMe.isChecked()) selected = "Chỉ mình tôi";

            selectedObject = selected;

            TextView txtObject = (TextView) btnObject.getChildAt(0);
            txtObject.setText(selected + " ▼");

            bottomSheetDialog.dismiss();
            updateButtonState();
        });

        bottomSheetDialog.show();
    }

    private void createPost() {
        String noiDung = edtPostContent.getText().toString().trim();
        if (noiDung.isEmpty()) {
            Toast.makeText(getActivity(), "Vui lòng nhập nội dung bài viết", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thông tin chủ đề và quyền riêng tư
        TextView txtTopic = btnTopic.findViewById(R.id.txtTopic);
        TextView txtAudience = btnObject.findViewById(R.id.txtObject);
        String ChuDe = txtTopic.getText().toString().replace(" ▼", "");
        String QuyenRiengTu = txtAudience.getText().toString().replace(" ▼", "");

        Integer maChuDe = getMaChuDe(ChuDe);
        if (maChuDe == -1 ) {
            maChuDe = null;
        }
        int maQuyenRiengTu = getMaQuyenRiengTu(QuyenRiengTu);
        if (maQuyenRiengTu == -1 ) {
            Toast.makeText(getActivity(), "Chưa chọn quyền riêng tư hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra nếu có ảnh và chuẩn bị phần ảnh
        List<MultipartBody.Part> imageParts = prepareImageParts();
        List<String> imageUrls = getImageUrlsFromUris();

        // Tạo đối tượng PostRequest (chỉ bao gồm text và metadata)
        PostRequest postRequest = new PostRequest(noiDung, maQuyenRiengTu, maChuDe, imageUrls);

        // Lấy authToken từ SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String authToken = preferences.getString("auth_token", "");

        // Kiểm tra nếu token không hợp lệ
        if (authToken.isEmpty()) {
            Toast.makeText(getActivity(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("CreatePost", "noiDung: " + noiDung);
        Log.d("CreatePost", "maChuDe: " + maChuDe);
        Log.d("CreatePost", "maQuyenRiengTu: " + maQuyenRiengTu);
        for (Uri uri : selectedImageUris) {
            Log.d("CreatePost", "Image URI: " + uri.toString());
        }

        // Thêm token vào header của yêu cầu
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<PostResponse> call = apiService.createPostWithImages("Bearer " + authToken, postRequest, imageParts);

        // Gọi API để tạo bài viết
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Bài viết đã được đăng thành công!", Toast.LENGTH_SHORT).show();
                    // Chuyển hướng hoặc thực hiện hành động khác sau khi đăng thành công
                } else {
                    Toast.makeText(getActivity(), "Đăng bài thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> getImageUrlsFromUris() {
        List<String> imageUrls = new ArrayList<>();
        for (Uri uri : selectedImageUris) {
            // Chuyển Uri thành URL ảnh
            String imageUrl = uri.toString(); // Hoặc bạn có thể tải ảnh lên server trước rồi lấy URL
            imageUrls.add(imageUrl);
        }
        return imageUrls;
    }

    private List<MultipartBody.Part> prepareImageParts() {
        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (Uri uri : selectedImageUris) {
            // Lấy file từ Uri
            File file = new File(getRealPathFromURI(uri));
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("images", file.getName(), requestBody);
            imageParts.add(part);
        }
        return imageParts;
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) return uri.getPath();
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        String path = cursor.getString(idx);
        cursor.close();
        return path;
    }



    private int getMaChuDe(String ChuDe){
        switch (ChuDe) {
            case "Kinh nghiệm":
                return 1;
            case "Đời sống":
                return 2;
            default:
                return -1;
        }
    }

    private int getMaQuyenRiengTu(String QuyenRiengTu) {
        switch (QuyenRiengTu) {
            case "Mọi người":
                return 1;
            case "Bạn bè":
                return 2;
            case "Chỉ mình tôi":
                return 3;
            default:
                return -1;
        }
    }
}
//    private List<String> getImageUrlsFromUris() {
//        List<String> imageUrls = new ArrayList<>();
//        for (Uri uri : selectedImageUris) {
//            // Chuyển Uri thành URL ảnh
//            String imageUrl = uri.toString(); // Hoặc bạn có thể tải ảnh lên server trước rồi lấy URL
//            imageUrls.add(imageUrl);
////            imageUrls.add("https://file.hstatic.net/200000709287/article/da_nang_ec42ae7a1fcf45159c8f78b5bd403d33_1024x1024.png");
//        }
//        return imageUrls;
//    }
//
//
//
//    private List<MultipartBody.Part> prepareImageParts() {
//        List<MultipartBody.Part> imageParts = new ArrayList<>();
//        for (Uri uri : selectedImageUris) {
//            // Lấy file từ Uri
//            File file = new File(getRealPathFromURI(uri));
//            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
//            MultipartBody.Part part = MultipartBody.Part.createFormData("images", file.getName(), requestBody);
//            imageParts.add(part);
//        }
//        return imageParts;
//    }
//
//
//    private String getRealPathFromURI(Uri uri) {
//        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
//        if (cursor == null) return uri.getPath();
//        cursor.moveToFirst();
//        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//        String path = cursor.getString(idx);
//        cursor.close();
//        return path;
//    }
//
//
//}
//
//
