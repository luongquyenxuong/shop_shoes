package com.example.shoes_shop.service.impl;

import com.example.shoes_shop.constant.Constant;
import com.example.shoes_shop.dto.CreatePostRequest;
import com.example.shoes_shop.entity.Post;
import com.example.shoes_shop.entity.User;
import com.example.shoes_shop.exception.BadRequestException;
import com.example.shoes_shop.exception.InternalServerException;
import com.example.shoes_shop.exception.NotFoundException;
import com.example.shoes_shop.repository.PostRepository;
import com.example.shoes_shop.service.PostService;
import com.github.slugify.Slugify;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

//    @Override
//    public Page<Post> adminGetListPost(String title, String status, int page) {
//        PageUtil pageUtil = new PageUtil(LIMIT_POST, page);
//
//        // Get list posts and totalItems
//        List<PostDTO> posts = postRepository.adminGetListPost(title, status, LIMIT_POST, pageUtil.calculateOffset());
//        int totalItems = postRepository.countPostFilter(title, status);
//
//        int totalPages = pageUtil.calculateTotalPage(totalItems);
//
//        return new PageableDTO(posts, totalPages, pageUtil.getPage());
//    }

    @Override
    public Post createPost(CreatePostRequest createPostRequest, User user) {
        Post post = new Post();

        post.setTitle(createPostRequest.getTitle());
        Slugify slg = new Slugify();
        post.setSlug(slg.slugify(createPostRequest.getTitle()));
        post.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        post.setCreatedBy(user);

        if (createPostRequest.getStatus() == Constant.PUBLIC_POST) {
            // Public post
            if (createPostRequest.getDescription().isEmpty()) {
                throw new BadRequestException("Để công khai bài viết vui lòng nhập mô tả");
            }
            if (createPostRequest.getImage().isEmpty()) {
                throw new BadRequestException("Vui lòng chọn ảnh cho bài viết trước khi công khai");
            }
            post.setPublishedAt(new Timestamp(System.currentTimeMillis()));
        } else {
            if (createPostRequest.getStatus() != Constant.DRAFT_POST) {
                throw new BadRequestException("Trạng thái bài viết không hợp lệ");
            }
        }
        post.setDescription(createPostRequest.getDescription());
        post.setThumbnail(createPostRequest.getImage());
        post.setStatus(createPostRequest.getStatus());

        postRepository.save(post);

        return post;
    }

    @Override
    public void updatePost(CreatePostRequest createPostRequest, User user, Long id) {
        Optional<Post> rs = postRepository.findById(id);
        if (rs.isEmpty()) {
            throw new NotFoundException("Bài viết không tồn tại");
        }
        Post post = rs.get();
        post.setTitle(createPostRequest.getTitle());
        Slugify slug = new Slugify();
        post.setSlug(slug.slugify(createPostRequest.getTitle()));
        post.setModifiedAt(new Timestamp(System.currentTimeMillis()));
        post.setModifiedBy(user);
        if (createPostRequest.getStatus() ==Constant.PUBLIC_POST) {
            // Public post
            if (createPostRequest.getDescription().isEmpty()) {
                throw new BadRequestException("Để công khai bài viết vui lòng nhập mô tả");
            }
            if (createPostRequest.getImage().isEmpty()) {
                throw new BadRequestException("Vui lòng chọn ảnh cho bài viết trước khi công khai");
            }
            if (post.getPublishedAt() == null) {
                post.setPublishedAt(new Timestamp(System.currentTimeMillis()));
            }
        } else {
            if (createPostRequest.getStatus() != Constant.DRAFT_POST) {
                throw new BadRequestException("Trạng thái bài viết không hợp lệ");
            }
        }
        post.setDescription(createPostRequest.getDescription());
        post.setThumbnail(createPostRequest.getImage());
        post.setStatus(createPostRequest.getStatus());

        try {
            postRepository.save(post);
        } catch (Exception ex) {
            throw new InternalServerException("Lỗi khi cập nhật bài viết");
        }
    }

    @Override
    public void deletePost(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new NotFoundException("Bài viết không tồn tại");
        }
        try {
            postRepository.deleteById(id);
        } catch (Exception ex) {
            throw new InternalServerException("Lỗi khi xóa bài viết");
        }
    }

    @Override
    public Post getPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new NotFoundException("Bài viết không tồn tại");
        }
        return post.get();
    }

    @Override
    public Page<Post> adminGetListPosts(String title, String status, Integer page) {
        page--;
        if (page < 0) {
            page = 0;
        }
        Pageable pageable = PageRequest.of(page, Constant.LIMIT_POST, Sort.by("published_at").descending());
        return postRepository.adminGetListPosts(title, status, pageable);
    }

    @Override
    public List<Post> getLatesPost() {
        return postRepository.getLatesPosts(Constant.PUBLIC_POST,Constant.LIMIT_POST_NEW);
    }

    @Override
    public Page<Post> getListPost(Integer page) {
        page--;
        if (page < 0) {
            page = 0;
        }
        Pageable pageable = PageRequest.of(page,Constant.LIMIT_POST_SHOP, Sort.by("publishedAt").descending());
        return postRepository.findAllByStatus(Constant.PUBLIC_POST, pageable);
    }

    @Override
    public List<Post> getLatestPostsNotId(Long id) {
        return postRepository.getLatestPostsNotId(Constant.PUBLIC_POST,id,Constant.LIMIT_POST_RELATED);
    }
//
    @Override
    public Long getCountPost() {
        return postRepository.count();
    }
}
