package com.noom.interview.fullstack.sleep.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "sleep_logs",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_sleeplogs_user_date",
                columnNames = {"user_id", "sleep_date"}
        )
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SleepLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "sleep_date", nullable = false)
    private LocalDate sleepDate;

    @Column(name = "bed_time", nullable = false)
    private Instant bedTime;

    @Column(name = "wake_time", nullable = false)
    private Instant wakeTime;

    @Column(name = "total_time_in_bed_minutes", nullable = false)
    private Integer totalTimeInBedMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "feeling", nullable = false, length = 10)
    private Feeling feeling;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
