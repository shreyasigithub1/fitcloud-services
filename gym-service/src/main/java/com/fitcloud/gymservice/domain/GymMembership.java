package com.fitcloud.gymservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "gym_membership")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GymMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long gymId;             // which gym

    @Column(nullable = false)
    private Long memberId;          // from identity service

    @Column(nullable = false)
    private Long planId;            // which plan chosen

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipStatus status; // ACTIVE, EXPIRED, CANCELLED

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;   // startDate + plan duration

    private LocalDateTime cancelledAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = MembershipStatus.ACTIVE;
        this.startDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
