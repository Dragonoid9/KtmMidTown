package com.rac.ktm.service;


import com.rac.ktm.entity.News;
import com.rac.ktm.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    public List<News> findAll() {
        return newsRepository.findAllByOrderByCreatedDateDesc();
    }

    public News findById(Long id) {
        return newsRepository.findById(id).orElseThrow(() -> new RuntimeException("News not found"));
    }

    public void save(News news, String currentUser) {
        if (news.getId() == null) {
            // Creating a new post
            news.setCreatedDate(LocalDateTime.now());
            news.setCreatedBy(currentUser);
            news.setUpdatedBy(null); // updatedBy is null on creation
        } else {
            // Updating an existing post
            News existingNews = newsRepository.findById(news.getId()).orElseThrow(() -> new RuntimeException("Event not found"));
            news.setCreatedDate(existingNews.getCreatedDate()); // Retain original created date
            news.setCreatedBy(existingNews.getCreatedBy()); // Retain original created by
            news.setUpdatedDate(LocalDateTime.now());
            news.setUpdatedBy(currentUser);
        }
        newsRepository.save(news);
    }

    public void deleteById(Long id) {
        newsRepository.deleteById(id);
    }
}
