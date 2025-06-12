package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.exception.ResourceNotFoundException;
import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.GuestCategory;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingInfo;
import com.kacper.wedding_planner.repository.GuestRepository;
import com.kacper.wedding_planner.repository.WeddingInfoRepository;
import com.kacper.wedding_planner.service.GuestService;
import com.kacper.wedding_planner.service.UserService;
import com.kacper.wedding_planner.service.WeddingInfoService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
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
    private final WeddingInfoRepository weddingInfoRepository;
    private final WeddingInfoService weddingInfoService;

    public WeddingController(GuestService guestService, GuestRepository guestRepository, UserService userService, WeddingInfoRepository weddingInfoRepository, WeddingInfoService weddingInfoService) {
        this.guestService = guestService;
        this.guestRepository = guestRepository;
        this.userService = userService;
        this.weddingInfoRepository = weddingInfoRepository;
        this.weddingInfoService = weddingInfoService;
    }

    @GetMapping
    public String listGuests(Model model, @AuthenticationPrincipal CustomUserDetails principal, @RequestParam(required = false) String kategoria) {

        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = userService.findByEmail(principal.getUsername());
        List<Guest> guests = guestRepository.findByUser(currentUser);

        if (kategoria != null && !kategoria.isEmpty()) {
            try {
                GuestCategory selectedCategory = GuestCategory.valueOf(kategoria);
                guests = guests.stream()
                        .filter(guest -> guest.getKategoria() == selectedCategory)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                System.out.println("Nieprawidłowa kategoria: " + kategoria);
            }
        }

        guests.sort(Comparator.comparing(Guest::getNazwisko));
        model.addAttribute("guests", guests);
        model.addAttribute("kategorie", GuestCategory.values());
        model.addAttribute("selectedCategory", kategoria);
        return "guests";
    }

    @GetMapping("/new")
    public String showCreateGuestForm(Model model) {
        Guest newGuest = new Guest();
        newGuest.setKategoria(null);
        model.addAttribute("guest", newGuest);
        model.addAttribute("categories", GuestCategory.values());
        return "add_guest";
    }

    @PostMapping
    public String addGuest(@Valid @ModelAttribute("guest") Guest guest,
                           BindingResult bindingResult,
                           @AuthenticationPrincipal CustomUserDetails principal,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", GuestCategory.values());
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
                              @AuthenticationPrincipal CustomUserDetails principal,
                              Model model) {

        User currentUser = userService.findByEmail(principal.getUsername());
        guest.setUser(currentUser);

        if (guest.getId() != null) {
            Guest existingGuest = guestRepository.findById(guest.getId()).orElseThrow();
            guest.setKategoria(existingGuest.getKategoria());
        }

        guestRepository.save(guest);
        return "redirect:/guests/receptions";
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
                                        @AuthenticationPrincipal CustomUserDetails principal) {
        User currentUser = userService.findByEmail(principal.getUsername());
        List<Guest> notConfirmedGuests = guestRepository.findByUserAndPotwierdzenieObecnosci(currentUser, "NIE");

        notConfirmedGuests.sort(Comparator.comparing(Guest::getNazwisko));
        model.addAttribute("notConfirmedGuests", notConfirmedGuests);
        return "not_confirmed_guests";
    }

    @GetMapping("/search")
    public String searchGuests(@RequestParam("nazwisko") String nazwisko, Model model,
                               @AuthenticationPrincipal CustomUserDetails principal) {
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
                                             @AuthenticationPrincipal CustomUserDetails principal) {
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

    @GetMapping("/countdown")
    public String getCountdownPage(Model model, @AuthenticationPrincipal CustomUserDetails principal) {
        User user = userService.findByEmail(principal.getUsername());
        WeddingInfo info = weddingInfoRepository.findByUser(user).orElse(new WeddingInfo());
        model.addAttribute("weddingInfo", info);
        return "countdown";

    }

    @PostMapping("/countdown")
    public String saveWeddingInfo(@ModelAttribute WeddingInfo weddingInfo,
                                  @AuthenticationPrincipal CustomUserDetails principal) {
        User user = userService.findByEmail(principal.getUsername());
        weddingInfoService.saveOrUpdateWeddingInfo(weddingInfo, user);
        return "redirect:/guests/countdown";
    }

}

