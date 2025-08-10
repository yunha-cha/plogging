package com.website.zubging.common.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "plogging")
public class Plogging {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ploggingId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
    private Long userId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "trail_id")
//    private Trail trail;
    private Long trailId;

    private LocalDate ploggingDate;
    private String ploggingTime;
    private Double distance;

    private Double startLat;
    private Double startLng;
    private Double endLat;
    private Double endLng;

//    @OneToMany(mappedBy = "plogging", cascade = CascadeType.ALL)
//    private List<PloggingTrash> trashList = new ArrayList<>();
}

