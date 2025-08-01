package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingInfo;
import com.kacper.wedding_planner.repository.WeddingInfoRepository;
import com.kacper.wedding_planner.service.WeddingInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class WeddingInfoServiceImpl implements WeddingInfoService {

    @Autowired
    private WeddingInfoRepository weddingInfoRepository;
    @Override
    public void saveOrUpdateWeddingInfo(WeddingInfo weddingInfo, User user) {
        Optional<WeddingInfo> existing = weddingInfoRepository.findByUser(user);

        if (existing.isPresent()) {
            WeddingInfo existingInfo = existing.get();
            existingInfo.setBrideName(weddingInfo.getBrideName());
            existingInfo.setGroomName(weddingInfo.getGroomName());
            existingInfo.setWeddingDate(weddingInfo.getWeddingDate());
            weddingInfoRepository.save(existingInfo);
        } else {
            weddingInfo.setUser(user);
            weddingInfoRepository.save(weddingInfo);
        }
    }
}
