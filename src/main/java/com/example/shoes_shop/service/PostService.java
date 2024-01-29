package com.example.shoes_shop.service;

import com.example.shoes_shop.dto.CreatePostRequest;
import com.example.shoes_shop.entity.Post;
import com.example.shoes_shop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;


public interface PostService {
//    Page<Post> adminGetListPost(String title, String status, Integer page);

    Post createPost(CreatePostRequest createPostRequest, User user);

    void updatePost(CreatePostRequest createPostRequest, User user, Long id);

    void deletePost(Long id);

    Post getPostById(Long id);

    Page<Post> adminGetListPosts(String title, String status, Integer page);

    List<Post> getLatesPost();
//
    Page<Post> getListPost(Integer page);
//
    List<Post> getLatestPostsNotId(Long id);
//
    Long getCountPost();
}
