package es.udc.asi.habits.repository;

import es.udc.asi.habits.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    
    List<Habit> findByUserId(String userId);
    
    List<Habit> findByUserIdAndStatus(String userId, Habit.HabitStatus status);
    
    List<Habit> findByUserIdAndCategoryId(String userId, Long categoryId);
    
    @Query("SELECT h FROM Habit h WHERE h.userId = :userId AND h.startDate >= :startDate AND h.startDate <= :endDate")
    List<Habit> findByUserIdAndDateRange(String userId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT h FROM Habit h WHERE h.userId = :userId AND (LOWER(h.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(h.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Habit> searchByUserIdAndNameOrDescription(String userId, String searchTerm);
}
