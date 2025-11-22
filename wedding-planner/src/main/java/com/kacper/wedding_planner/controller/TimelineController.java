package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingTimeline;
import com.kacper.wedding_planner.service.UserService;
import com.kacper.wedding_planner.service.WeddingInfoService;
import com.kacper.wedding_planner.service.WeddingTimelineService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/timeline")
public class TimelineController {

    private final WeddingTimelineService weddingTimelineService;
    private final UserService userService;

    public TimelineController(WeddingTimelineService weddingTimelineService, UserService userService) {
        this.weddingTimelineService = weddingTimelineService;
        this.userService = userService;
    }
    @GetMapping
    public String showTimeline(@AuthenticationPrincipal CustomUserDetails principal, Model model) {

        User user = userService.findByEmail(principal.getUsername());

        model.addAttribute("timelines", weddingTimelineService.getForUser(user));
        model.addAttribute("timeline", new WeddingTimeline());

        return "timeline";
    }
}
