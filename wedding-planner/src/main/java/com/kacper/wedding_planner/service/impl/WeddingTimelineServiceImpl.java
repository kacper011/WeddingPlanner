package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingTimeline;
import com.kacper.wedding_planner.repository.WeddingTimelineRepository;
import com.kacper.wedding_planner.service.WeddingTimelineService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
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

    @Transactional
    @Override
    public void deleteForUser(Long id, User user) {
        WeddingTimeline timeline = weddingTimelineRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono wpisu osi czasu"));

        weddingTimelineRepository.delete(timeline);
    }

    @Override
    public WeddingTimeline findByIdForUser(Long id, User user) {
        return weddingTimelineRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono wpisu osi czasu"));
    }
}


