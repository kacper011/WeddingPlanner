package com.kacper.wedding_planner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "upload_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiresAt;
    private Integer maxUses;
    private Integer uses = 0;
    private boolean active = true;
    public boolean isValid() {
        boolean notExpired = expiresAt == null || LocalDateTime.now().isBefore(expiresAt);
        boolean underMaxUses = maxUses == null || uses < maxUses;
        return notExpired && underMaxUses;
    }

    public static UploadToken createForUser(User user, int maxUses) {
        UploadToken t = new UploadToken();
        t.setOwner(user);
        t.setToken(UUID.randomUUID().toString());
        t.setActive(true);
        t.setMaxUses(maxUses);
        t.setCreatedAt(LocalDateTime.now());
        return t;
    }
}
