package com.rac.ktm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name="podcast")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Podcast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String link;

    private String createdBy;
    private String updatedBy;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

}
