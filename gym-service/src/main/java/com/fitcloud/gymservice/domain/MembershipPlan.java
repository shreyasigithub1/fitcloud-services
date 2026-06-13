package com.fitcloud.gymservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Entity
@Table(name = "membership_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String planName;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanDuration planDuration;  // MONTHLY, QUARTERLY, ANNUAL

    @Column(nullable = false)
    private Integer durationInDays;     // 30, 90, 365(calculated from duration)

    private String features;            // "Pool access, Parking, Locker"

    @Column(nullable = false)
    private Boolean isActive;   // owner can enable/disable plan

    @Column(nullable = false)
    private Long gymId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.durationInDays = calculateDays();

    }
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.durationInDays = calculateDays();
    }

    private Integer calculateDays() {
        if (planDuration == null) return 0;
        return
                switch (planDuration) {
                    case MONTHLY -> 30;
                    case QUARTERLY -> 90;
                    case ANNUAL -> 365;

                };
    }

}
