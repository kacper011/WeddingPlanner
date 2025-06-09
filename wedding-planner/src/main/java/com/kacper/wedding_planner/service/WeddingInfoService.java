package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingInfo;

public interface WeddingInfoService {

    void saveOrUpdateWeddingInfo(WeddingInfo weddingInfo, User user);
}
