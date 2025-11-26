package com.kacper.wedding_planner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    private String filename;
    private String storagePath;
    private String contentType;
    private Long size;
    private boolean uploadedByPublic;
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Transient
    public String getUrl() {
        return "/gallery/image/" + this.id;
    }
}
