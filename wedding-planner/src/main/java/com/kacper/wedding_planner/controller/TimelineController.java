package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.dto.WeddingTimelineRequest;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingTimeline;
import com.kacper.wedding_planner.service.UserService;
import com.kacper.wedding_planner.service.WeddingTimelineService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
        model.addAttribute("timelineRequest", new WeddingTimelineRequest());

        return "timeline";
    }

    @PostMapping
    public String addTimeline(@Valid @ModelAttribute("timelineRequest") WeddingTimelineRequest request,
                              BindingResult result,
                              @AuthenticationPrincipal CustomUserDetails principal,
                              Model model) {

        User user = userService.findByEmail(principal.getUsername());

        if (result.hasErrors()) {
            model.addAttribute("timelines", weddingTimelineService.getForUser(user));
            return "timeline";
        }

        WeddingTimeline timeline = new WeddingTimeline();
        timeline.setTitle(request.getTitle());
        timeline.setTime(request.getTime());
        timeline.setUser(user);

        weddingTimelineService.save(timeline);

        return "redirect:/timeline";
    }

    @PostMapping("/delete/{id}")
    public String deleteTimeline(@PathVariable Long id,
                                 @AuthenticationPrincipal CustomUserDetails principal) {

        User user = userService.findByEmail(principal.getUsername());
        WeddingTimeline timeline = weddingTimelineService.findById(id);

        if (timeline == null || !timeline.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Forbidden");
        }

        weddingTimelineService.delete(id);

        return "redirect:/timeline";
    }
}
