package com.example.shoes_shop.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "title",nullable = false,length = 300)
    private String title;

    @Column(name = "description",columnDefinition = "TEXT")
    private String description;
    @Column(name = "slug",nullable = false,length = 600)
    private String slug;
    @Column(name = "thumbnail")
    private String thumbnail;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "modified_at")
    private Timestamp modifiedAt;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    @ManyToOne
    @JoinColumn(name = "modified_by")
    private User modifiedBy;
    @Column(name = "published_at")
    private Timestamp publishedAt;
    @Column(name = "status",columnDefinition = "int default 0")
    private int status;
    @OneToMany(mappedBy = "post")
    private List<Comment> comments;
}