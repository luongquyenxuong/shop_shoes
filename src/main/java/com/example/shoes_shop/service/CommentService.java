package com.example.shoes_shop.service;

import com.example.shoes_shop.dto.CreateCommentPostRequest;
import com.example.shoes_shop.dto.CreateCommentProductRequest;
import com.example.shoes_shop.entity.Comment;

import java.util.UUID;

public interface CommentService {
    Comment createCommentPost(CreateCommentPostRequest createCommentPostRequest, UUID userId);
    Comment createCommentProduct(CreateCommentProductRequest createCommentProductRequest, UUID userId);
}
