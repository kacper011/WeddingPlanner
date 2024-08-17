package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.service.GuestService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/guests")
public class WeddingController {

    private GuestService guestService;

    public WeddingController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping
    public String listGuests(Model model) {
        List<Guest> guests = guestService.getAllGuests();
        model.addAttribute("guests", guests);
        return "guests";
    }



    @GetMapping("/new")
    public String showCreateGuestForm(Model model) {
        model.addAttribute("guest", new Guest());
        return "add_guest";
    }

    @PostMapping
    public String addGuest(@Valid @ModelAttribute("guest") Guest guest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "add_guest";
        }

        guestService.saveGuest(guest);
        return "redirect:/guests";
    }
}
