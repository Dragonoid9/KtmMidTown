package com.rac.ktm.repository;


import com.rac.ktm.entity.Podcast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PodcastRepository extends JpaRepository<Podcast, Long> {
    void deleteById(Long id);

    Optional<Podcast> findById(Long id);

    List<Podcast> findAll();

    List<Podcast> findAllByOrderByCreatedDateDesc();
}
