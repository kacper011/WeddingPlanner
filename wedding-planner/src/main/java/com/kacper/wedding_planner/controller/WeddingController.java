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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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
    public String listGuests(Model model, @AuthenticationPrincipal CustomUserDetails principal, @RequestParam(required = false) String category) {
        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = userService.findByEmail(principal.getUsername());
        List<Guest> guests = guestRepository.findByUser(currentUser);

        if (category != null && !category.isEmpty()) {
            try {
                GuestCategory selectedCategory = GuestCategory.valueOf(category);
                guests = guests.stream()
                        .filter(guest -> guest.getCategory() == selectedCategory)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                System.out.println("Nieprawidłowa kategoria: " + category);
            }
        }

        guests.sort(Comparator.comparing(Guest::getLastName));

        long totalGuests = guests.size();
        long confirmedGuests = guests.stream().filter(g -> "TAK".equalsIgnoreCase(g.getAttendanceConfirmation())).count();
        long notConfirmedGuests = guests.stream().filter(g -> "NIE".equalsIgnoreCase(g.getAttendanceConfirmation())).count();
        long receptionGuests = guests.stream().filter(g -> "TAK".equalsIgnoreCase(g.getAfterParty())).count();

        model.addAttribute("guests", guests);
        model.addAttribute("categories", GuestCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("totalGuests", totalGuests);
        model.addAttribute("confirmedGuests", confirmedGuests);
        model.addAttribute("notConfirmedGuests", notConfirmedGuests);
        model.addAttribute("receptionGuests", receptionGuests);

        return "guests";
    }

    @GetMapping("/new")
    public String showCreateGuestForm(Model model) {
        Guest newGuest = new Guest();
        newGuest.setCategory(null);
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

    @PostMapping("/create")
    public String createGuest(@ModelAttribute @Valid Guest guest,
                              BindingResult result,
                              Model model,
                              @AuthenticationPrincipal CustomUserDetails principal) {
        if (result.hasErrors()) {
            model.addAttribute("categories", GuestCategory.values());
            return "add_guest";
        }

        guest.setUser(userService.findByEmail(principal.getUsername()));
        guestRepository.save(guest);
        return "redirect:/guests";
    }

    @PostMapping("/edit/{id}")
    public String editGuest(@PathVariable Long id,
                            @ModelAttribute Guest guest,
                            @AuthenticationPrincipal CustomUserDetails principal) {
        try {
            Guest existingGuest = guestRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Guest not found with id: " + id));

            guest.setId(id);
            guest.setUser(userService.findByEmail(principal.getUsername()));
            guest.setCategory(existingGuest.getCategory());
            guestRepository.save(guest);

            return "redirect:/guests/confirmed";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
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
        guest.setAttendanceConfirmation(presence);

        if ("NIE".equals(presence)) {
            guest.setTransport(null);
            guest.setAccommodation(null);
        }

        guestRepository.save(guest);
        return "redirect:/guests";
    }

    @PostMapping("/updateTransport/{id}")
    public String updateTransport(@PathVariable Long id, @RequestParam String transport) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guest not found"));
        guest.setTransport(transport);
        guestRepository.save(guest);
        return "redirect:/guests";
    }

    @PostMapping("/updateLodging/{id}")
    public String updateLodging(@PathVariable Long id, @RequestParam String lodging) {
        Guest guest = guestRepository.findById(id).orElseThrow();
        guest.setAccommodation(lodging);
        guestRepository.save(guest);
        return "redirect:/guests";
    }

    @GetMapping("/confirmed")
    public String getConfirmedGuests(Model model,
                                     @AuthenticationPrincipal CustomUserDetails principal) {
        User currentUser = userService.findByEmail(principal.getUsername());
        List<Guest> confirmedGuests = guestRepository.findByUserAndAttendanceConfirmation(currentUser, "TAK");

        confirmedGuests.sort(Comparator.comparing(Guest::getLastName));
        model.addAttribute("confirmedGuests", confirmedGuests);
        return "confirmed_guests";
    }

    @GetMapping("/notConfirmed")
    public String getNotConfirmedGuests(Model model,
                                        @AuthenticationPrincipal CustomUserDetails principal) {
        User currentUser = userService.findByEmail(principal.getUsername());
        List<Guest> notConfirmedGuests = guestRepository.findByUserAndAttendanceConfirmation(currentUser, "NIE");

        notConfirmedGuests.sort(Comparator.comparing(Guest::getLastName));
        model.addAttribute("notConfirmedGuests", notConfirmedGuests);
        return "not_confirmed_guests";
    }

    @GetMapping("/search")
    public String searchGuests(@RequestParam("lastName") String lastName, Model model,
                               @AuthenticationPrincipal CustomUserDetails principal) {
        User currentUser = userService.findByEmail(principal.getUsername());
        List<Guest> guests = guestRepository.findByUser(currentUser);

        List<Guest> filteredGuests = guests.stream()
                .filter(guest -> guest.getLastName().toLowerCase().contains(lastName.toLowerCase()))
                .collect(Collectors.toList());

        model.addAttribute("guests", filteredGuests);
        return "guests";
    }

    @GetMapping("/receptions")
    public String getWeddingReceptionsGuests(Model model,
                                             @AuthenticationPrincipal CustomUserDetails principal) {
        User currentUser = userService.findByEmail(principal.getUsername());
        List<Guest> weddingReceptionsGuests = guestRepository.findByUserAndAfterParty(currentUser, "TAK");

        model.addAttribute("weddingReceptionsGuests", weddingReceptionsGuests);
        return "wedding_receptions";
    }

    @PostMapping("/receptions/toggle/{id}")
    public String toggleReceptionStatus(@PathVariable Long id) {
        Optional<Guest> optionalGuest = guestRepository.findById(id);

        optionalGuest.ifPresent(guest -> {
            if ("TAK".equals(guest.getAfterParty())) {
                guest.setAfterParty("NIE");
            } else {
                guest.setAfterParty("TAK");
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

