package com.example.shoes_shop.service.impl;

import com.example.shoes_shop.dto.CreateCommentPostRequest;
import com.example.shoes_shop.dto.CreateCommentProductRequest;
import com.example.shoes_shop.entity.Comment;
import com.example.shoes_shop.entity.Post;
import com.example.shoes_shop.entity.Product;
import com.example.shoes_shop.entity.User;
import com.example.shoes_shop.exception.InternalServerException;
import com.example.shoes_shop.repository.CommentRepository;
import com.example.shoes_shop.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServeImpl implements CommentService {
    private final CommentRepository commentRepository;
    @Override
    public Comment createCommentPost(CreateCommentPostRequest createCommentPostRequest, UUID userId) {
        Comment comment = new Comment();
        Post post = new Post();
        post.setId(createCommentPostRequest.getPostId());
        comment.setPost(post);
        User user = new User();
        user.setId(userId);
        comment.setUser(user);
        comment.setContent(createCommentPostRequest.getContent());
        comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        try {
            commentRepository.save(comment);
        } catch (Exception e) {
            throw new InternalServerException("Có lỗi trong quá trình bình luận!");
        }
        return comment;
    }

    @Override
    public Comment createCommentProduct(CreateCommentProductRequest createCommentProductRequest, UUID userId) {
        Comment comment = new Comment();
        comment.setContent(createCommentProductRequest.getContent());
        comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        User user = new User();
        user.setId(userId);
        comment.setUser(user);
        Product product = new Product();
        product.setId(createCommentProductRequest.getProductId());
        comment.setProduct(product);
        try {
            commentRepository.save(comment);
        } catch (Exception e) {
            throw new InternalServerException("Có lỗi trong quá trình bình luận!");
        }
        return comment;
    }
}
