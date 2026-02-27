package es.udc.asi.habits.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "habit_completions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"habit_id", "completion_date"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    @JsonIgnore
    private Habit habit;

    @Column(nullable = false)
    private LocalDate completionDate;

    @Column(nullable = false)
    private boolean completed; // true si se completó ese día, false si no

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        completedAt = LocalDateTime.now();
        if (completionDate == null) {
            completionDate = LocalDate.now();
        }
    }
}
