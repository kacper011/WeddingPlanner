package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.GuestTable;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.GuestRepository;
import com.kacper.wedding_planner.repository.GuestTableRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import com.kacper.wedding_planner.service.GuestTableService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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


        List<GuestTable> tables = guestTableService.getTablesForUser(user);
        List<Guest> guests = guestRepository.findByUser(user);

        model.addAttribute("tables", tables);
        model.addAttribute("allGuests", guests);

        return "tables";
    }

    @PostMapping("/assign")
    public String assignGuestToTable(@RequestParam Long guestId, @RequestParam Long tableId, @AuthenticationPrincipal CustomUserDetails principal) {

        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new RuntimeException("Guest not found"));
        GuestTable table = guestTableRepository.findById(tableId).orElseThrow(() -> new RuntimeException("Table not found"));

        if (!guest.getUser().getId().equals(user.getId()) ||
            !table.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Forbidden");
        }
        guest.setTable(table);
        guestRepository.save(guest);

        return "redirect:/tables";
    }
    @Transactional
    @PostMapping("/add")
    public String addTable(@RequestParam String name,
                           @RequestParam(defaultValue = "100") int positionX,
                           @RequestParam(defaultValue = "100") int positionY,
                           @RequestParam String shape,
                           @AuthenticationPrincipal CustomUserDetails principal) {

        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        GuestTable newTable = new GuestTable();
        newTable.setName(name);
        newTable.setPositionX(positionX);
        newTable.setPositionY(positionY);
        newTable.setShape(shape);
        newTable.setUser(user);

        guestTableRepository.save(newTable);

        return "redirect:/tables";
    }

    @PostMapping("/update-position")
    @ResponseBody
    public ResponseEntity<?> updateTablePosition(@RequestBody Map<String, Object> payload,
                                                 @AuthenticationPrincipal CustomUserDetails principal) {
        try {
            Long tableId = Long.valueOf(payload.get("id").toString());
            int posX = ((Number) payload.get("posX")).intValue();
            int posY = ((Number) payload.get("posY")).intValue();

            User user = userRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

            GuestTable table = guestTableRepository.findById(tableId)
                    .orElse(null);


            if (table == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "error", "message", "Nie znaleziono stołu"));
            }

            if (!table.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("status", "error", "message", "Nie masz uprawnień do tej tabeli"));
            }

            table.setPositionX(posX);
            table.setPositionY(posY);
            guestTableRepository.save(table);

            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public String deleteTable(@RequestParam Long id, @AuthenticationPrincipal CustomUserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        guestTableService.deleteTableById(id, user);

        return "redirect:/tables";
    }
}
