package com.mindtogether.goals.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "habit_id", nullable = false)
    private Long habitId;

    @Column(name = "habit_name", length = 200)
    private String habitName;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "target_value", nullable = false)
    private Integer targetValue;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "personal_reward", length = 500)
    private String personalReward;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private GoalStatus status = GoalStatus.ACTIVE;

    @Column(name = "current_progress", nullable = false)
    @Builder.Default
    private Integer currentProgress = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (currentProgress == null) {
            currentProgress = 0;
        }
        if (status == null) {
            status = GoalStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Transient
    public double getProgressPercentage() {
        if (targetValue == 0) {
            return 0.0;
        }
        return Math.min(100.0, (currentProgress * 100.0) / targetValue);
    }

    @Transient
    public boolean isOverdue() {
        return status == GoalStatus.ACTIVE && LocalDate.now().isAfter(targetDate);
    }

    @Transient
    public long getDaysRemaining() {
        if (status != GoalStatus.ACTIVE) {
            return 0;
        }
        return LocalDate.now().until(targetDate).getDays();
    }

    public enum GoalStatus {
        ACTIVE,      // Objetivo activo
        COMPLETED,   // Objetivo completado
        FAILED,      // Objetivo fallido (no alcanzado en la fecha límite)
        CANCELLED    // Objetivo cancelado por el usuario
    }
}

