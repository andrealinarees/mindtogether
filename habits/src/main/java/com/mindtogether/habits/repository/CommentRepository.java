package com.mindtogether.habits.repository;

import com.mindtogether.habits.model.Comment;
import com.mindtogether.habits.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByHabit(Habit habit);
    List<Comment> findByHabitOrderByCreatedAtDesc(Habit habit);
}


