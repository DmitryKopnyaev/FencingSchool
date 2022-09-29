package com.kopnyaev.fschool.controller;

import com.kopnyaev.fschool.model.Trainer;
import com.kopnyaev.fschool.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainer")
public class TrainerController {

    private TrainerRepository trainerRepository;

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity addTrainer(@RequestBody Trainer trainer) {
        try {
            this.trainerRepository.save(trainer);
            return new ResponseEntity<>(trainer, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity updateTrainer(@RequestBody Trainer trainer) {
        long id = trainer.getId();
        Trainer trainerById = this.trainerRepository.findById(id).orElse(null);
        if (trainerById == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        else {
            try {
                this.trainerRepository.save(trainer);
                return new ResponseEntity<>(trainerById, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Trainer with this name, surname, patronymic is already in the database", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Trainer> getTrainerById(@PathVariable long id) {
        Trainer trainer = this.trainerRepository.findById(id).orElse(null);
        if (trainer == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        else return new ResponseEntity<>(trainer, HttpStatus.OK);
    }

    @GetMapping(produces = "application/json")
    public List<Trainer> getAllTrainers() {
        return this.trainerRepository.findAll();
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Trainer> deleteTrainerById(@PathVariable long id) {
        Trainer trainer = this.trainerRepository.findById(id).orElse(null);
        if (trainer == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        else {
            this.trainerRepository.deleteById(id);
            return new ResponseEntity<>(trainer, HttpStatus.OK);
        }
    }
}
