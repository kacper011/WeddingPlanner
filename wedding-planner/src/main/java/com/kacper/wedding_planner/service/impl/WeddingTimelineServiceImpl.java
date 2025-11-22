package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingTimeline;
import com.kacper.wedding_planner.repository.WeddingTimelineRepository;
import com.kacper.wedding_planner.service.WeddingTimelineService;

import java.util.List;

public class WeddingTimelineServiceImpl implements WeddingTimelineService {

    private final WeddingTimelineRepository weddingTimelineRepository;

    public WeddingTimelineServiceImpl(WeddingTimelineRepository weddingTimelineRepository) {
        this.weddingTimelineRepository = weddingTimelineRepository;
    }
    @Override
    public List<WeddingTimeline> getForUser(User user) {
        return weddingTimelineRepository.findByUserOrderByTimeAsc(user);
    }

    @Override
    public void save(WeddingTimeline item) {
        weddingTimelineRepository.save(item);
    }

    @Override
    public void delete(Long id) {
        weddingTimelineRepository.deleteById(id);
    }
}
