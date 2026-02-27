package es.udc.asi.habits.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "habits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String userId; // ID del usuario propietario

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency frequency; // DAILY, WEEKLY

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate; // Fecha de fin del hábito

    private LocalTime habitTime; // Hora a la que hacer el hábito (opcional)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Location location; // INTERIOR, EXTERIOR

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HabitStatus status; // ACTIVE, COMPLETED, PAUSED

    private Long categoryId; // Referencia a la categoría (opcional)

    private Integer currentStreak; // Racha actual (días consecutivos)

    private Integer longestStreak; // Racha más larga alcanzada

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HabitCompletion> completions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = HabitStatus.ACTIVE;
        }
        if (currentStreak == null) {
            currentStreak = 0;
        }
        if (longestStreak == null) {
            longestStreak = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    public enum Frequency {
        DAILY, WEEKLY
    }

    public enum Location {
        INTERIOR, EXTERIOR
    }

    public enum HabitStatus {
        ACTIVE, COMPLETED, PAUSED
    }
}
