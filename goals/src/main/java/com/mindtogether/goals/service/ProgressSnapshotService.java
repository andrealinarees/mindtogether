package com.mindtogether.goals.service;

import com.mindtogether.goals.model.MentalHealthGoal;
import com.mindtogether.goals.model.ProgressSnapshot;
import com.mindtogether.goals.repository.MentalHealthGoalRepository;
import com.mindtogether.goals.repository.ProgressSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressSnapshotService {

    private final ProgressSnapshotRepository snapshotRepository;
    private final MentalHealthGoalRepository goalRepository;

    // ==================== Query Methods ====================

    public List<ProgressSnapshot> getSnapshotsByGoal(Long goalId, String userId) {
        // Verificar que la meta pertenece al usuario
        Optional<MentalHealthGoal> goal = goalRepository.findByIdAndUserId(goalId, userId);
        if (goal.isEmpty()) {
            return List.of();
        }
        
        return snapshotRepository.findByGoalIdOrderBySnapshotDateAsc(goalId);
    }

    public List<ProgressSnapshot> getSnapshotsByDateRange(Long goalId, String userId, 
                                                            LocalDate startDate, LocalDate endDate) {
        // Verificar que la meta pertenece al usuario
        Optional<MentalHealthGoal> goal = goalRepository.findByIdAndUserId(goalId, userId);
        if (goal.isEmpty()) {
            return List.of();
        }
        
        return snapshotRepository.findByGoalIdAndDateRange(goalId, startDate, endDate);
    }

    public List<ProgressSnapshot> getMilestoneSnapshots(String userId) {
        return snapshotRepository.findMilestoneSnapshotsByUser(userId);
    }

    public Optional<ProgressSnapshot> getLatestSnapshot(Long goalId, String userId) {
        // Verificar que la meta pertenece al usuario
        Optional<MentalHealthGoal> goal = goalRepository.findByIdAndUserId(goalId, userId);
        if (goal.isEmpty()) {
            return Optional.empty();
        }
        
        return snapshotRepository.findTopByGoalIdOrderBySnapshotDateDesc(goalId);
    }

    // ==================== Creation ====================

    @Transactional
    public ProgressSnapshot createSnapshot(Long goalId, String userId, ProgressSnapshot snapshot) {
        Optional<MentalHealthGoal> goalOpt = goalRepository.findByIdAndUserId(goalId, userId);
        
        if (goalOpt.isEmpty()) {
            throw new IllegalArgumentException("Goal not found");
        }
        
        MentalHealthGoal goal = goalOpt.get();
        snapshot.setGoal(goal);
        snapshot.setSnapshotDate(LocalDate.now());
        snapshot.setProgressValue(goal.getCurrentProgress());
        snapshot.setProgressPercentage(goal.getProgressPercentage());
        snapshot.setDaysActive(goal.getDaysActive());
        snapshot.setDaysRemaining(goal.getDaysRemaining());
        
        return snapshotRepository.save(snapshot);
    }

    // ==================== Analytics ====================

    public ProgressAnalytics getProgressAnalytics(Long goalId, String userId, int days) {
        Optional<MentalHealthGoal> goalOpt = goalRepository.findByIdAndUserId(goalId, userId);
        if (goalOpt.isEmpty()) {
            throw new IllegalArgumentException("Goal not found");
        }
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        List<ProgressSnapshot> snapshots = snapshotRepository.findByGoalIdAndDateRange(
                goalId, startDate, endDate);
        
        if (snapshots.isEmpty()) {
            return new ProgressAnalytics(0, 0.0, 0.0, null, null, List.of());
        }
        
        double avgMood = snapshots.stream()
                .filter(s -> s.getMoodRating() != null)
                .mapToInt(ProgressSnapshot::getMoodRating)
                .average()
                .orElse(0.0);
        
        double totalProgressChange = snapshots.get(snapshots.size() - 1).getProgressPercentage() - 
                                      snapshots.get(0).getProgressPercentage();
        
        LocalDate bestDay = snapshots.stream()
                .max((s1, s2) -> Double.compare(
                        s2.getProgressPercentage() - s1.getProgressPercentage(),
                        0))
                .map(ProgressSnapshot::getSnapshotDate)
                .orElse(null);
        
        LocalDate worstMoodDay = snapshots.stream()
                .filter(s -> s.getMoodRating() != null)
                .min((s1, s2) -> s1.getMoodRating().compareTo(s2.getMoodRating()))
                .map(ProgressSnapshot::getSnapshotDate)
                .orElse(null);
        
        return new ProgressAnalytics(
                snapshots.size(),
                totalProgressChange,
                avgMood,
                bestDay,
                worstMoodDay,
                snapshots
        );
    }

    public Map<LocalDate, Double> getMoodTrend(String userId, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        List<ProgressSnapshot> snapshots = snapshotRepository.findByUserAndDate(userId, startDate);
        
        return snapshots.stream()
                .filter(s -> s.getMoodRating() != null)
                .collect(Collectors.groupingBy(
                        ProgressSnapshot::getSnapshotDate,
                        Collectors.averagingInt(s -> s.getMoodRating() != null ? s.getMoodRating() : 0)
                ));
    }

    // ==================== Inner Class for Analytics ====================

    public record ProgressAnalytics(
            int totalSnapshots,
            double progressChange,
            double averageMood,
            LocalDate bestProgressDay,
            LocalDate lowestMoodDay,
            List<ProgressSnapshot> snapshots
    ) {}
}

