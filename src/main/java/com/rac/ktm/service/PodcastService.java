package com.rac.ktm.service;



import com.rac.ktm.entity.Podcast;
import com.rac.ktm.repository.PodcastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PodcastService {

    @Autowired
    private PodcastRepository podcastRepository;

    public List<Podcast> findAll() {
        return podcastRepository.findAllByOrderByCreatedDateDesc();
    }

    public Podcast findById(Long id) {
        return podcastRepository.findById(id).orElseThrow(() -> new RuntimeException("Podcast not found"));
    }

    public void save(Podcast podcast, String currentUser) {
        if (podcast.getId() == null) {
            // Creating a new post
            podcast.setCreatedDate(LocalDateTime.now());
            podcast.setCreatedBy(currentUser);
            podcast.setUpdatedBy(null); // updatedBy is null on creation
        } else {
            // Updating an existing post
            Podcast existingPodcast = podcastRepository.findById(podcast.getId()).orElseThrow(() -> new RuntimeException("Event not found"));
            podcast.setCreatedDate(existingPodcast.getCreatedDate()); // Retain original created date
            podcast.setCreatedBy(existingPodcast.getCreatedBy()); // Retain original created by
            podcast.setUpdatedDate(LocalDateTime.now());
            podcast.setUpdatedBy(currentUser);
        }
        podcastRepository.save(podcast);
    }

    public void deleteById(Long id) {
        podcastRepository.deleteById(id);
    }


}
