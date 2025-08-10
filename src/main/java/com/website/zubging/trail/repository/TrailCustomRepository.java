package com.website.zubging.trail.repository;

import com.website.zubging.common.entity.Trail;

import java.util.List;

public interface TrailCustomRepository {

    List<Trail> findAllByTrailTypeNameOrTrailName(String keyword);
}
