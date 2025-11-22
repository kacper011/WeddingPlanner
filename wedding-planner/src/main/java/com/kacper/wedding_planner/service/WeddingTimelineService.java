package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingTimeline;

import java.util.List;

public interface WeddingTimelineService {

    List<WeddingTimeline> getForUser(User user);

    void save(WeddingTimeline item);

    void delete(Long id);
}
