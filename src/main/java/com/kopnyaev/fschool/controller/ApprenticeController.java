package com.kopnyaev.fschool.controller;

import com.kopnyaev.fschool.model.Apprentice;
import com.kopnyaev.fschool.repository.ApprenticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/apprentice")
public class ApprenticeController {
    private ApprenticeRepository apprenticeRepository;

    @Autowired
    public void setApprenticeRepository(ApprenticeRepository apprenticeRepository) {
        this.apprenticeRepository = apprenticeRepository;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Apprentice> addApprentice(@RequestBody Apprentice apprentice) {
        try {
            this.apprenticeRepository.save(apprentice);
            return new ResponseEntity<>(apprentice, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Apprentice> getApprenticeById(@PathVariable long id) {
        Apprentice apprentice = this.apprenticeRepository.findById(id).orElse(null);
        if (apprentice == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        else return new ResponseEntity<>(apprentice, HttpStatus.OK);
    }

    @GetMapping(produces = "application/json")
    public List<Apprentice> getAllApprentices() {
        return this.apprenticeRepository.findAll();
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity updateApprentice(@RequestBody Apprentice apprentice) {
        long id = apprentice.getId();
        Apprentice apprenticeById = this.apprenticeRepository.findById(id).orElse(null);
        if (apprenticeById == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        else {
            try {
                this.apprenticeRepository.save(apprentice);
                return new ResponseEntity<>(apprentice, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("this phone number is already in the database", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Apprentice> deleteApprenticeById(@PathVariable long id) {
        Apprentice apprenticeById = this.apprenticeRepository.findById(id).orElse(null);
        if (apprenticeById == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        else {
            this.apprenticeRepository.deleteById(id);
            return new ResponseEntity<>(apprenticeById, HttpStatus.OK);
        }
    }
}
