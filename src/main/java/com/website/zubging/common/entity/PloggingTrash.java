package com.website.zubging.common.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "plogging_trash")
public class PloggingTrash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ploggingTrashId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "plogging_id")
//    private Plogging plogging;
    private Long ploggingId;

    private String description;
    private int count;
    private String amount;
    private String imageUrl;
}