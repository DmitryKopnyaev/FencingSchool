package com.kopnyaev.fschool.repository;

import com.kopnyaev.fschool.model.TrainerSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerScheduleRepository extends JpaRepository<TrainerSchedule, Long> {
}
