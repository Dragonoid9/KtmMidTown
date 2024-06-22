package com.rac.ktm.repository;

import com.rac.ktm.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface PostRepository extends JpaRepository<Post, Long> {
    void deleteById(Long id);

    Optional<Post> findById(Long id);

    List<Post> findAll();

    List<Post> findAllByOrderByCreatedDateDesc();

}