//package com.example.social_app.viewmodel;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.ViewModel;
//
//import com.example.social_app.model.BaiViet;
//import com.example.social_app.respository.PostRepository;
//
//import java.util.List;
//
//public class PostViewModel extends ViewModel {
//    private PostRepository postRepository;
//    private LiveData<List<BaiViet>> posts;
//
//    public PostViewModel() {
//        postRepository = new PostRepository();
//        posts = postRepository.getPosts();
//    }
//
//    public LiveData<List<BaiViet>> getPosts() {
//        return posts;
//    }
//}
