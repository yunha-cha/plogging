package com.website.zubging.plogging.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

// 플로깅 등록
@Data
public class PloggingDto {

    private Double startLat;
    private Double startLng;
    private Double endLat;
    private Double endLng;
    private LocalDate ploggingDate;
    private String ploggingTime;
    private Double distance;
    private String difficulty;
    private List<PloggingTrashDto> pickedTrashList;

    private Long userId;
    private Long trailId;
}
