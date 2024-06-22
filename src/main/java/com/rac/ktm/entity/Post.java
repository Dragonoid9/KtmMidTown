package com.rac.ktm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name="posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDate date;
    private String location;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String category;
    private String status;

    private String createdBy;
    private String updatedBy;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;
}
