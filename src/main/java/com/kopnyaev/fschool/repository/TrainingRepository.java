package com.kopnyaev.fschool.repository;

import com.kopnyaev.fschool.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    List<Training> getAllByNumberGymAndDate(int number, LocalDate date);

    List<Training> getByTrainerIdAndDate(long trainerId, LocalDate date);
}
