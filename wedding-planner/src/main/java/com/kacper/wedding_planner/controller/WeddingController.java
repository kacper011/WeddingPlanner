package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.exception.ResourceNotFoundException;
import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.repository.GuestRepository;
import com.kacper.wedding_planner.service.GuestService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/guests")
public class WeddingController {

    private GuestService guestService;
    private GuestRepository guestRepository;

    public WeddingController(GuestService guestService, GuestRepository guestRepository) {
        this.guestService = guestService;
        this.guestRepository = guestRepository;
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

    @GetMapping("/details/{id}")
    public String viewGuestDetails(@PathVariable("id") Long id, Model model) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found"));

        model.addAttribute("guest", guest);
        return "guest_details";
    }

    @PostMapping("/update")
    public String updateGuest(@ModelAttribute Guest guest) {
        guestRepository.save(guest);
        return "redirect:/guests";
    }

    @GetMapping("/delete/{id}")
    public String deleteGuest(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        System.out.println("Attempting to delete guest with id: " + id);
        guestService.deleteGuest(id);
        redirectAttributes.addFlashAttribute("message", "Gość został pomyślnie usunięty.");
        return "redirect:/guests";
    }

    @PostMapping("/updatePresence/{id}")
    public String updatePresence(@PathVariable Long id, @RequestParam String presence) {

        guestService.updatePresence(id, presence);

        if ("TAK".equals(presence)) {
            return "redirect:/guests/confirmed";
        } else {
            return "redirect:/guests/notConfirmed";
        }
    }

    @PostMapping("/updateTransport/{id}")
    public String updateTransport(@PathVariable Long id, @RequestParam String transport) {

        return "redirect:/guests";
    }

    @PostMapping("/updateLodging/{id}")
    public String updateLodging(@PathVariable Long id, @RequestParam String lodging) {

        return "redirect:/guests";
    }

    @GetMapping("/confirmed")
    public String confirmedGuests(Model model) {
        model.addAttribute("guests", guestService.findByConfirmedPresence("TAK"));
        return "confirmed_guests";
    }

    @GetMapping("/notConfirmed")
    public String notConfirmedGuests(Model model) {

        return "not_confirmed_guests";
    }
}
