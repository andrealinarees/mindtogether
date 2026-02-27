package es.udc.asi.habits.repository;

import es.udc.asi.habits.model.Habit;
import es.udc.asi.habits.model.HabitCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {
    
    List<HabitCompletion> findByHabitIdOrderByCompletionDateDesc(Long habitId);
    
    Optional<HabitCompletion> findByHabitIdAndCompletionDate(Long habitId, LocalDate completionDate);
    
    Optional<HabitCompletion> findByHabitAndCompletionDate(Habit habit, LocalDate completionDate);

    @Query("SELECT hc FROM HabitCompletion hc WHERE hc.habit.id = :habitId AND hc.completionDate >= :startDate AND hc.completionDate <= :endDate ORDER BY hc.completionDate")
    List<HabitCompletion> findByHabitIdAndDateRange(Long habitId, LocalDate startDate, LocalDate endDate);
    
    Long countByHabitId(Long habitId);

    Long countByCompletionDate(LocalDate completionDate);
}
