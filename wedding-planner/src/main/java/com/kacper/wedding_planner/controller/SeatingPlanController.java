package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.GuestTable;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.GuestRepository;
import com.kacper.wedding_planner.repository.GuestTableRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import com.kacper.wedding_planner.service.GuestTableService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/tables")
public class SeatingPlanController {
    private final GuestTableService guestTableService;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final GuestTableRepository guestTableRepository;

    public SeatingPlanController(GuestTableService guestTableService, UserRepository userRepository, GuestRepository guestRepository, GuestTableRepository guestTableRepository) {
        this.guestTableService = guestTableService;
        this.userRepository = userRepository;
        this.guestRepository = guestRepository;
        this.guestTableRepository = guestTableRepository;
    }

    @GetMapping
    public String showSeatingPlan(@AuthenticationPrincipal CustomUserDetails principal, Model model) {

        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));


        List<GuestTable> tables = guestTableService.getTableForUser(user);
        List<Guest> guests = guestRepository.findByUser(user);

        model.addAttribute("stoly", tables);
        model.addAttribute("allGuests", guests);

        return "tables";
    }

    @PostMapping("/assign")
    public String assignGuestToTable(@RequestParam Long guestId, @RequestParam Long tableId) {
        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new RuntimeException("Guest not found"));
        GuestTable table = guestTableRepository.findById(tableId).orElseThrow(() -> new RuntimeException("Table not found"));

        guest.setTable(table);
        guestRepository.save(guest);

        return "redirect:/tables";
    }

    @PostMapping("/add")
    public String addTable(@RequestParam String nazwa,
                           @RequestParam(defaultValue = "100") int pozycjaX,
                           @RequestParam(defaultValue = "100") int pozycjaY,
                           @AuthenticationPrincipal CustomUserDetails principal) {

        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        GuestTable newTable = new GuestTable();
        newTable.setNazwa(nazwa);
        newTable.setPozycjaX(pozycjaX);
        newTable.setPozycjaY(pozycjaY);
        newTable.setUser(user);

        guestTableRepository.save(newTable);

        return "redirect:/tables";
    }
}
