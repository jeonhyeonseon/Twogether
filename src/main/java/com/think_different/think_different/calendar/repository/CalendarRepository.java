package com.think_different.think_different.calendar.repository;

import com.think_different.think_different.calendar.entity.Calendar;
import com.think_different.think_different.couple.domain.Couple;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    List<Calendar> findByCoupleAndScheduleDateBetweenOrderByScheduleDateAsc(Couple couple, LocalDate startDate, LocalDate endDate);

    List<Calendar> findByCoupleAndScheduleDateOrderByStartTimeAsc(Couple couple, LocalDate scheduleDate);
}
