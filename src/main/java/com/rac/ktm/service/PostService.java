package com.rac.ktm.service;

import com.rac.ktm.entity.Post;
import com.rac.ktm.entity.UserInfo;
import com.rac.ktm.repository.PostRepository;
import com.rac.ktm.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public void save(Post post, String currentUser) {
        if (post.getId() == null) {
            // Creating a new post
            post.setCreatedDate(LocalDateTime.now());
            post.setCreatedBy(currentUser);
            post.setUpdatedBy(null); // updatedBy is null on creation
        } else {
            // Updating an existing post
            Post existingPost = postRepository.findById(post.getId()).orElseThrow(() -> new RuntimeException("Event not found"));
            post.setCreatedDate(existingPost.getCreatedDate()); // Retain original created date
            post.setCreatedBy(existingPost.getCreatedBy()); // Retain original created by
            post.setUpdatedDate(LocalDateTime.now());
            post.setUpdatedBy(currentUser);
        }
        postRepository.save(post);
    }

    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }
}
