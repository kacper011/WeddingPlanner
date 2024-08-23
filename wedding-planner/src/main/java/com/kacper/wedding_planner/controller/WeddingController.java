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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

        List<Guest> sortedGuests = guests.stream()
                        .sorted(Comparator.comparing(Guest::getNazwisko))
                                .collect(Collectors.toList());


        model.addAttribute("guests", sortedGuests);
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

    @PostMapping("/delete/{id}")
    public String deleteGuest(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        System.out.println("Attempting to delete guest with id: " + id);
        guestService.deleteGuest(id);
        redirectAttributes.addFlashAttribute("message", "Gość został pomyślnie usunięty.");
        return "redirect:/guests";
    }

    @PostMapping("/updatePresence/{id}")
    public String updatePresence(@PathVariable("id") Long id, @RequestParam("presence") String presence) {
        Guest guest = guestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid guest Id:" + id));
        guest.setPotwierdzenieObecnosci(presence);

        if ("NIE".equals(presence)) {
            guest.setTransport(null); 
            guest.setNocleg(null);
        }

        guestRepository.save(guest);
        return "redirect:/guests";
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
    public String getConfirmedGuests(Model model) {
        List<Guest> confirmedGuests = guestRepository.findByPotwierdzenieObecnosci("TAK");

        List<Guest> sortedConfirmedGuests = confirmedGuests.stream()
                        .sorted(Comparator.comparing(Guest::getNazwisko))
                                .collect(Collectors.toList());

        model.addAttribute("confirmedGuests", sortedConfirmedGuests);
        return "confirmed_guests";
    }

    @GetMapping("/notConfirmed")
    public String getNotConfirmedGuests(Model model) {
        List<Guest> notConfirmedGuests = guestRepository.findByPotwierdzenieObecnosci("NIE");

        List<Guest> sortedNotConfirmedGuests = notConfirmedGuests.stream()
                        .sorted(Comparator.comparing(Guest::getNazwisko))
                                .collect(Collectors.toList());

        model.addAttribute("notConfirmedGuests", sortedNotConfirmedGuests);
        return "not_confirmed_guests";
    }

    @GetMapping("/search")
    public String searchGuests(@RequestParam("nazwisko") String nazwisko, Model model) {
        List<Guest> guests = guestRepository.findAll();

        List<Guest> filteredGuests = guests.stream()
                .filter(guest -> guest.getNazwisko().toLowerCase().contains(nazwisko.toLowerCase()))
                .collect(Collectors.toList());

        model.addAttribute("guests", filteredGuests);

        return "guests";
    }

    @GetMapping("/receptions")
    public String getWeddingReceptionsGuests(Model model) {

        List<Guest> weddingReceptionsGuests = guestRepository.findByPoprawiny("TAK");
        model.addAttribute("weddingReceptionsGuests", weddingReceptionsGuests);

        return "wedding_receptions";
    }
}
