package es.udc.asi.habits.repository;

import es.udc.asi.habits.model.Comment;
import es.udc.asi.habits.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByHabit(Habit habit);
    List<Comment> findByHabitOrderByCreatedAtDesc(Habit habit);
}

