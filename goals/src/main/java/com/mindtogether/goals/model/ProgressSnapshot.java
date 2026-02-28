package com.mindtogether.goals.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Instantánea del progreso de una meta en un momento específico
 * Permite visualizar el progreso histórico y generar gráficas
 */
@Entity
@Table(name = "progress_snapshots", 
       indexes = {
           @Index(name = "idx_snapshot_goal_date", columnList = "goal_id, snapshot_date")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private MentalHealthGoal goal;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "progress_value", nullable = false)
    private Integer progressValue;  // Progreso en ese momento

    @Column(name = "progress_percentage", nullable = false)
    private Double progressPercentage;

    @Column(name = "days_active", nullable = false)
    private Long daysActive;  // Días que lleva activa la meta

    @Column(name = "days_remaining", nullable = false)
    private Long daysRemaining;  // Días restantes hasta la fecha límite

    @Column(name = "mood_rating")
    private Integer moodRating;  // Estado de ánimo del usuario ese día (1-10)

    @Column(name = "notes", length = 1000)
    private String notes;  // Notas opcionales del usuario sobre su progreso

    @Column(name = "milestone_reached")
    private Boolean milestoneReached;  // Si se alcanzó un hito ese día

    @Column(name = "milestone_id")
    private Long milestoneId;  // ID del hito alcanzado (si aplica)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (snapshotDate == null) {
            snapshotDate = LocalDate.now();
        }
    }

    @Transient
    public boolean isSignificantProgress() {
        // Considera "progreso significativo" si avanzó más del 10%
        return progressPercentage >= 10.0;
    }

    @Transient
    public String getProgressDescription() {
        if (progressPercentage >= 100.0) {
            return "¡Meta completada!";
        } else if (progressPercentage >= 75.0) {
            return "Casi terminando";
        } else if (progressPercentage >= 50.0) {
            return "A mitad de camino";
        } else if (progressPercentage >= 25.0) {
            return "Buen progreso";
        } else if (progressPercentage > 0.0) {
            return "Comenzando";
        } else {
            return "Sin progreso aún";
        }
    }
}

