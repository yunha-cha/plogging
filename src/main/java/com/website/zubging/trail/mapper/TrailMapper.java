package com.website.zubging.trail.mapper;

import com.website.zubging.trail.dto.TrailDto;
import com.website.zubging.trail.dto.TrailWithDistanceDto;
import com.website.zubging.common.entity.Trail;

public class TrailMapper {

    public static TrailDto toTrailDto(Trail trail) {
        if (trail == null) return null;

        return TrailDto.builder()
                .trailId(trail.getTrailId())
                .trailName(trail.getTrailName())
                .trailTypeName(trail.getTrailTypeName())
                .length(trail.getLength())
                .lengthDetail(trail.getLengthDetail())
                .difficultyLevel(trail.getDifficultyLevel())
                .trackTime(trail.getTrackTime())
                .cityName(trail.getCityName())
                .lotNumberAddress(trail.getLotNumberAddress())
                .spotLatitude(trail.getSpotLatitude())
                .spotLongitude(trail.getSpotLongitude())
                .amenityDescription(trail.getAmenityDescription())
                .toiletDescription(trail.getToiletDescription())
                .optionDescription(trail.getOptionDescription())
                .description(trail.getDescription())
                .descriptionDetail(trail.getDescriptionDetail())
                .reportCount(trail.getReportCount())
                .build();
    }


    public static TrailWithDistanceDto toTrailWithDistanceDto(Trail trail, double distanceToUser){
        if (trail == null) return null;

        return TrailWithDistanceDto.builder()
                .trailId(trail.getTrailId())
                .trailName(trail.getTrailName())
                .trailTypeName(trail.getTrailTypeName())
                .length(trail.getLength())
                .lengthDetail(trail.getLengthDetail())
                .difficultyLevel(trail.getDifficultyLevel())
                .trackTime(trail.getTrackTime())
                .cityName(trail.getCityName())
                .lotNumberAddress(trail.getLotNumberAddress())
                .spotLatitude(trail.getSpotLatitude())
                .spotLongitude(trail.getSpotLongitude())
                .amenityDescription(trail.getAmenityDescription())
                .toiletDescription(trail.getToiletDescription())
                .optionDescription(trail.getOptionDescription())
                .description(trail.getDescription())
                .descriptionDetail(trail.getDescriptionDetail())
                .reportCount(trail.getReportCount())
                .distanceToUser(distanceToUser)
                .build();
    }


}
