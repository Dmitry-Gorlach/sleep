package com.noom.interview.fullstack.sleep.repository;

import com.noom.interview.fullstack.sleep.domain.entity.SleepLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

/**
 * Repository for managing {@link SleepLog} entities.
 */
@Repository
public interface SleepLogRepository extends JpaRepository<SleepLog, Long> {

    /**
     * Finds the most recent sleep log for a specific user.
     *
     * @param userId the ID of the user
     * @return an Optional containing the most recent SleepLog, or empty if none exists
     */
    Optional<SleepLog> findFirstByUserIdOrderBySleepDateDesc(UUID userId);

    /**
     * Finds all sleep logs for a specific user within a date range (inclusive).
     *
     * @param userId the ID of the user
     * @param start the start date (inclusive)
     * @param end the end date (inclusive)
     * @return a list of SleepLog entities within the date range
     */
    List<SleepLog> findByUserIdAndSleepDateBetween(UUID userId, LocalDate start, LocalDate end);

    /**
     * Checks if a sleep log exists for a specific user on a specific date.
     *
     * @param userId the ID of the user
     * @param date the date to check
     * @return true if a sleep log exists, false otherwise
     */
    boolean existsByUserIdAndSleepDate(UUID userId, LocalDate date);
}