package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.exception.ResourceNotFoundException;
import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.GuestRepository;
import com.kacper.wedding_planner.service.GuestService;
import com.kacper.wedding_planner.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/guests")
public class WeddingController {

    private final GuestService guestService;
    private final GuestRepository guestRepository;
    private final UserService userService;

    public WeddingController(GuestService guestService, GuestRepository guestRepository, UserService userService) {
        this.guestService = guestService;
        this.guestRepository = guestRepository;
        this.userService = userService;
    }

    @GetMapping
    public String listGuests(Model model, @AuthenticationPrincipal CustomUserDetails principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = userService.findByEmail(principal.getUsername());
        List<Guest> guests = guestRepository.findByUser(currentUser);

        guests.sort(Comparator.comparing(Guest::getNazwisko));
        model.addAttribute("guests", guests);
        return "guests";
    }

    @GetMapping("/new")
    public String showCreateGuestForm(Model model) {
        model.addAttribute("guest", new Guest());
        return "add_guest";
    }

    @PostMapping
    public String addGuest(@Valid @ModelAttribute("guest") Guest guest,
                           BindingResult bindingResult,
                           @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        if (bindingResult.hasErrors()) {
            return "add_guest";
        }

        User currentUser = userService.findByEmail(principal.getUsername());
        guest.setUser(currentUser);
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
    public String updateGuest(@ModelAttribute Guest guest,
                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        User currentUser = userService.findByEmail(principal.getUsername());
        guest.setUser(currentUser); // zabezpieczenie przed utratą powiązania
        guestRepository.save(guest);
        return "redirect:/guests";
    }

    @PostMapping("/delete/{id}")
    public String deleteGuest(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        guestService.deleteGuest(id);
        redirectAttributes.addFlashAttribute("message", "Gość został pomyślnie usunięty.");
        return "redirect:/guests";
    }

    @PostMapping("/updatePresence/{id}")
    public String updatePresence(@PathVariable("id") Long id, @RequestParam("presence") String presence) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid guest Id:" + id));
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
        Guest guest = guestRepository.findById(id).orElseThrow();
        guest.setTransport(transport);
        guestRepository.save(guest);
        return "redirect:/guests";
    }

    @PostMapping("/updateLodging/{id}")
    public String updateLodging(@PathVariable Long id, @RequestParam String lodging) {
        Guest guest = guestRepository.findById(id).orElseThrow();
        guest.setNocleg(lodging);
        guestRepository.save(guest);
        return "redirect:/guests";
    }

    @GetMapping("/confirmed")
    public String getConfirmedGuests(Model model,
                                     @AuthenticationPrincipal CustomUserDetails principal) {
        User currentUser = userService.findByEmail(principal.getUsername());
        List<Guest> confirmedGuests = guestRepository.findByUserAndPotwierdzenieObecnosci(currentUser, "TAK");

        confirmedGuests.sort(Comparator.comparing(Guest::getNazwisko));
        model.addAttribute("confirmedGuests", confirmedGuests);
        return "confirmed_guests";
    }

    @GetMapping("/notConfirmed")
    public String getNotConfirmedGuests(Model model,
                                        @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        User currentUser = userService.findByEmail(principal.getUsername());
        List<Guest> notConfirmedGuests = guestRepository.findByUserAndPotwierdzenieObecnosci(currentUser, "NIE");

        notConfirmedGuests.sort(Comparator.comparing(Guest::getNazwisko));
        model.addAttribute("notConfirmedGuests", notConfirmedGuests);
        return "not_confirmed_guests";
    }

    @GetMapping("/search")
    public String searchGuests(@RequestParam("nazwisko") String nazwisko, Model model,
                               @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        User currentUser = userService.findByEmail(principal.getUsername());
        List<Guest> guests = guestRepository.findByUser(currentUser);

        List<Guest> filteredGuests = guests.stream()
                .filter(guest -> guest.getNazwisko().toLowerCase().contains(nazwisko.toLowerCase()))
                .collect(Collectors.toList());

        model.addAttribute("guests", filteredGuests);
        return "guests";
    }

    @GetMapping("/receptions")
    public String getWeddingReceptionsGuests(Model model,
                                             @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        User currentUser = userService.findByEmail(principal.getUsername());
        List<Guest> weddingReceptionsGuests = guestRepository.findByUserAndPoprawiny(currentUser, "TAK");

        model.addAttribute("weddingReceptionsGuests", weddingReceptionsGuests);
        return "wedding_receptions";
    }

    @PostMapping("/receptions/toggle/{id}")
    public String toggleReceptionStatus(@PathVariable Long id) {
        Optional<Guest> optionalGuest = guestRepository.findById(id);

        optionalGuest.ifPresent(guest -> {
            if ("TAK".equals(guest.getPoprawiny())) {
                guest.setPoprawiny("NIE");
            } else {
                guest.setPoprawiny("TAK");
            }
            guestRepository.save(guest);
        });

        return "redirect:/guests/receptions";
    }
}

