package com.kopnyaev.fschool.controller;

import com.kopnyaev.fschool.model.Schedule;
import com.kopnyaev.fschool.model.Trainer;
import com.kopnyaev.fschool.model.TrainerSchedule;
import com.kopnyaev.fschool.repository.TrainerRepository;
import com.kopnyaev.fschool.repository.TrainerScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
public class TrainerScheduleController {
    private TrainerRepository trainerRepository;
    private TrainerScheduleRepository trainerScheduleRepository;

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setTrainerScheduleRepository(TrainerScheduleRepository trainerScheduleRepository) {
        this.trainerScheduleRepository = trainerScheduleRepository;
    }

    @PostMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<TrainerSchedule> addTrainerSchedule(@PathVariable long id,
                                                              @RequestParam String weekDay,
                                                              @RequestParam String start,
                                                              @RequestParam String end) {
        Trainer trainer = this.trainerRepository.findById(id).orElse(null);
        if (trainer == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        TrainerSchedule trainerSchedule = trainer.getTrainerSchedule();
        if (trainerSchedule == null)
            trainerSchedule = new TrainerSchedule(trainer);
        trainerSchedule.addSchedule(weekDay, start, end);
        try {
            this.trainerScheduleRepository.save(trainerSchedule);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<>(trainerSchedule, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<TrainerSchedule> getTrainerScheduleByTrainerId(@PathVariable long id) {
        Trainer trainer = this.trainerRepository.findById(id).orElse(null);
        if (trainer == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        else {
            TrainerSchedule trainerSchedule = trainer.getTrainerSchedule();
            return new ResponseEntity<>(trainerSchedule, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/list/{id}", produces = "application/json")
    public ResponseEntity<List<Schedule>> getTrainerScheduleList(@PathVariable long id) {
        TrainerSchedule trainerSchedule = this.getTrainerScheduleByTrainerId(id).getBody();
        if (trainerSchedule == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        else {
            return new ResponseEntity<>(trainerSchedule.scheduleList(), HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<TrainerSchedule> deleteByTrainerIdByWeekDay(@PathVariable long id, @RequestParam String weekDay) {
        Trainer trainer = this.trainerRepository.findById(id).orElse(null);
        if (trainer == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        TrainerSchedule trainerSchedule = trainer.getTrainerSchedule();
        if (trainerSchedule == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        trainerSchedule.deleteSchedule(weekDay);
        this.trainerScheduleRepository.save(trainerSchedule);
        return new ResponseEntity<>(trainerSchedule, HttpStatus.OK);
    }
}
