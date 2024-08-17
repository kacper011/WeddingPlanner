package com.kacper.wedding_planner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "goscie")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nazwisko", nullable = false)
    private String nazwisko;

    @Column(name = "imie", nullable = false)
    private String imie;

    @Column(name = "potwierdzeni_obecnosci")
    private String potwierdzenieObecnosci;

    @Column(name = "transport")
    private String transport;

    @Column(name = "nocleg")
    private String nocleg;

    @Column(name = "kontakt")
    private String kontakt;

    @Column(name = "informacje")
    private String dodatkoweInformacje;
}
