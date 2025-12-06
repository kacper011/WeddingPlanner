package com.kacper.wedding_planner.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tables")
public class GuestTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "The table name cannot be empty")
    @Size(max = 50, message = "The table name can be up to 50 characters long")
    private String name;
    @NotNull
    @Min(value = 0, message = "Position X cannot be negative")
    @Max(value = 5000, message = "Position X is too large")
    private Integer positionX;
    @NotNull
    @Min(value = 0, message = "Position Y cannot be negative")
    @Max(value = 5000, message = "Position Y is too large")
    private Integer positionY;
    @NotBlank
    @Pattern(
            regexp = "circle|rectangle",
            message = "The shape of the table must be 'circle' or 'rectangle'"
    )
    private String shape;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;
    @OneToMany(mappedBy = "table")
    private List<Guest> guests;

}
