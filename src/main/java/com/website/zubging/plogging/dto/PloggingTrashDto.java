package com.website.zubging.plogging.dto;

import lombok.Data;

@Data
public class PloggingTrashDto {

    private Long ploggingTrashId;
    private String description;
    private int count;
    private String amount;
    private String imageUrl;
}
