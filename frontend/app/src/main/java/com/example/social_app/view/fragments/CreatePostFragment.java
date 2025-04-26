package com.example.social_app.view.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.social_app.R;
import com.example.social_app.model.CloudinaryUploadResult;
import com.example.social_app.model.PostRequest;
import com.example.social_app.model.PostResponse;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.CloudinaryService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.activities.LoginActivity;
import com.example.social_app.view.activities.PostActivity;
import com.example.social_app.view.adapters.ImageAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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

                        if (!selectedImageUris.isEmpty() || imageAdapter.getCount() > 0) {
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
        boolean hasObject = !selectedObject.isEmpty();
        btnPost.setEnabled(hasContent && hasObject);
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

    private void createPost() {
        String noiDung = edtPostContent.getText().toString().trim();
        if (noiDung.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập nội dung bài viết", Toast.LENGTH_SHORT).show();
            return;
        }

        String chuDe = ((TextView) btnTopic.getChildAt(0)).getText().toString().replace(" ▼", "");
        String quyenRiengTu = ((TextView) btnObject.getChildAt(0)).getText().toString().replace(" ▼", "");

        int maChuDe = getMaChuDe(chuDe);
        if (maChuDe == -1) {
            Toast.makeText(getContext(), "Chưa chọn chủ đề hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        int maQuyenRiengTu = getMaQuyenRiengTu(quyenRiengTu);
        if (maQuyenRiengTu == -1) {
            Toast.makeText(getContext(), "Chưa chọn quyền riêng tư hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }


        SharedPreferences preferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String authToken = preferences.getString("auth_token", "");

        if (authToken.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gửi request lên server
        sendPostToServer(authToken, noiDung, maQuyenRiengTu, maChuDe);
    }

    private void sendPostToServer(String authToken, String noiDung, int maQuyenRiengTu, Integer maChuDe) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Các phần text
        RequestBody noiDungBody = RequestBody.create(MediaType.parse("text/plain"), noiDung);
        RequestBody quyenBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(maQuyenRiengTu));
        RequestBody chuDeBody = RequestBody.create(MediaType.parse("text/plain"), maChuDe != null ? String.valueOf(maChuDe) : "");

        // Ảnh (multipart)
        List<MultipartBody.Part> imageParts = new ArrayList<>();
        if (selectedImageUris != null) {
            for (Uri uri : selectedImageUris) {
                try {
                    File resizedImage = resizeImage(uri); // ✅ resize ảnh trước khi gửi
                    RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), resizedImage);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("images", resizedImage.getName(), fileBody);
                    imageParts.add(part);
                } catch (IOException e) {
                    Log.e("CreatePost", "Lỗi xử lý ảnh: " + e.getMessage());
                }
            }
        }

        Call<PostResponse> call = apiService.createPost(
                "Bearer " + authToken,
                noiDungBody,
                quyenBody,
                chuDeBody,
                imageParts
        );

        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                    // TODO: Chuyển màn hoặc làm gì tiếp
                    // Chuyển sang PostFragment và truyền dữ liệu bài viết mới

//                    PostResponse createdPost = response.body();
//
//
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("new_post", createdPost); // PostResponse phải implement Serializable
//
//                    PostFragment postFragment = new PostFragment();
//                    postFragment.setArguments(bundle);

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new PostFragment())
                            .commit();


                } else {
                    Toast.makeText(getActivity(), "Đăng bài thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File resizeImage(Uri imageUri) throws IOException {
        // Bước 1: Đọc bitmap gốc từ URI
        Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

        // Bước 2: Resize ảnh
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 800, 800, true);

        // Bước 3: Lưu bitmap đã resize vào file tạm
        File file = new File(getActivity().getCacheDir(), getFileName(imageUri));
        FileOutputStream out = new FileOutputStream(file);
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out); // nén ảnh xuống 80% chất lượng
        out.flush();
        out.close();

        return file;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME);
                        result = cursor.getString(nameIndex);
                    }
                } finally {
                    cursor.close();
                }
            }
        }

        if (result == null) {
            String path = uri.getPath();
            int cut = path != null ? path.lastIndexOf('/') : -1;
            if (cut != -1) {
                result = path.substring(cut + 1);
            }
        }
        return result;
    }


}
