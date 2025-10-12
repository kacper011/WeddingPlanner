package com.kacper.wedding_planner.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "guests")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name is required")
    private String firstName;

    @Column(name = "attendance_confirmation")
    private String attendanceConfirmation;

    @Column(name = "transport")
    private String transport;

    @Column(name = "accommodation")
    private String accommodation;

    @Column(name = "after_party")
    private String afterParty;

    @Column(name = "contact")
    @Pattern(regexp = "\\d{3} \\d{3} \\d{3}", message = "Phone number must have format xxx xxx xxx")
    private String contact;

    @Column(name = "additional_info")
    private String additionalInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "guest_category")
    @NotNull(message = "Please select a category")
    private GuestCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private GuestTable table;
}
