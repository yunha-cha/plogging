package com.website.zubging.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column(nullable = false, length = 255)
    private String originalFilename;

    @Column(nullable = false, length = 512)
    private String s3Key;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false, length = 100)
    private String fileType;

    @Column(nullable = false)
    private LocalDateTime uploadDate;

    // User 엔티티
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

    @PrePersist
    public void prePersist() {
        this.uploadDate = LocalDateTime.now();
    }
}