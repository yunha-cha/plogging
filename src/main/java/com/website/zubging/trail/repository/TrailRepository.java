package com.website.zubging.trail.repository;

import com.website.zubging.common.entity.Trail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrailRepository extends JpaRepository<Trail, Long>, JpaSpecificationExecutor<Trail> {

    @Query("SELECT t FROM Trail t WHERE t.trailTypeName LIKE %:keyword% OR t.trailName LIKE %:keyword% OR t.cityName LIKE %:keyword%")
    List<Trail> findAllByTrailTypeNameOrTrailNameOrCityName(String keyword);
}
