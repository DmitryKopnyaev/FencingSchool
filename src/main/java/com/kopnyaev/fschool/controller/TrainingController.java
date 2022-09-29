package com.kopnyaev.fschool.controller;

import com.kopnyaev.fschool.model.Apprentice;
import com.kopnyaev.fschool.model.Schedule;
import com.kopnyaev.fschool.model.Trainer;
import com.kopnyaev.fschool.model.Training;
import com.kopnyaev.fschool.repository.ApprenticeRepository;
import com.kopnyaev.fschool.repository.TrainerRepository;
import com.kopnyaev.fschool.repository.TrainingRepository;
import com.kopnyaev.fschool.util.LocalTimeRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/training")
public class TrainingController {
    private TrainerRepository trainerRepository;
    private ApprenticeRepository apprenticeRepository;
    private TrainingRepository trainingRepository;

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setApprenticeRepository(ApprenticeRepository apprenticeRepository) {
        this.apprenticeRepository = apprenticeRepository;
    }

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @GetMapping(value = "/jymdate", produces = "application/json")
    public List<Training> getAllByNumberJymAndDate(@RequestParam int number, @RequestParam String date) {
        LocalDate local = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return this.trainingRepository.getAllByNumberGymAndDate(number, local);
    }

    @GetMapping(value = "/byapprentice/{id}", produces = "application/json")
    public ResponseEntity<List<Training>> findAllByApprenticeId(@PathVariable long id) {
        Apprentice apprentice = this.apprenticeRepository.findById(id).orElse(null);
        if (apprentice == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        List<Training> trainings = apprentice.getTrainings();
        return new ResponseEntity<>(trainings, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Training> deleteById(@PathVariable long id) {
        Training training = this.trainingRepository.findById(id).orElse(null);
        if (training == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        this.trainingRepository.deleteById(id);
        return new ResponseEntity<>(training, HttpStatus.OK);
    }

    @PostMapping(value = "/{idTrainer}/{idApprentice}", produces = "application/json")
    public ResponseEntity add(@PathVariable long idTrainer, @PathVariable long idApprentice,
                              @RequestParam int gymNumber, @RequestParam String date, @RequestParam String start) {
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
        if (date.isEmpty())
            return new ResponseEntity<>("Date is empty", HttpStatus.BAD_REQUEST);
        if (start.isEmpty())
            return new ResponseEntity<>("Start time is empty", HttpStatus.BAD_REQUEST);
        if (gymNumber <= 0)
            return new ResponseEntity<>("Gym number is negative", HttpStatus.BAD_REQUEST);

        LocalDate localDate = LocalDate.parse(date, formatterDate);
        LocalTime localTime = LocalTime.parse(start, formatterTime);

        Apprentice apprentice = this.apprenticeRepository.findById(idApprentice).orElse(null);
        if (apprentice == null)
            return new ResponseEntity<>("Apprentice not found", HttpStatus.BAD_REQUEST);

        Trainer trainer = this.trainerRepository.findById(idTrainer).orElse(null);
        if (trainer == null)
            return new ResponseEntity<>("Trainer not found", HttpStatus.BAD_REQUEST);

        //toDo проверка на работу тренера (на часы работы)
        List<Schedule> schedules = trainer.getTrainerSchedule().scheduleList();
        Schedule schedule = schedules.stream().filter(o -> o.getWeekDay().equalsIgnoreCase(localDate.getDayOfWeek().toString())).findFirst().orElse(null);
        if (schedule == null || schedule.getBegin() == null || schedule.getEnd() == null)
            return new ResponseEntity<>("No trainer schedule in this day",
                    HttpStatus.BAD_REQUEST);
        if (localTime.isBefore(schedule.getBegin()) || localTime.isAfter(schedule.getEnd().minusMinutes(90)))
            return new ResponseEntity<>("The training time out of range trainer schedule",
                    HttpStatus.BAD_REQUEST);

        List<Training> trainingsTrainerDay = this.trainingRepository.getByTrainerIdAndDate(idTrainer, localDate);
        int count = this.countOverlaps(trainingsTrainerDay, localTime);

        if (count == 3)
            return new ResponseEntity<>("Trainer has already have training with 3 apprentice this time",
                    HttpStatus.BAD_REQUEST);

        if (this.trainingRepository.getAllByNumberGymAndDate(gymNumber, localDate).size() == 10)
            return new ResponseEntity<>("No more than 10 apprentices in gym", HttpStatus.BAD_REQUEST);

        if (apprentice.getTrainings().stream().anyMatch(o -> o.getDate().equals(localDate)))
            return new ResponseEntity<>("Apprentice has already have training in this day", HttpStatus.BAD_REQUEST);

        Training training = new Training(gymNumber, trainer, apprentice, localDate, localTime);
        try {
            this.trainingRepository.save(training);
            return new ResponseEntity<>(training, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Duplicate training", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/listtimes/{idTrainer}", produces = "application/json")
    public ResponseEntity getWorkingTime(@PathVariable long idTrainer, @RequestParam String date) {
        Trainer trainer = this.trainerRepository.findById(idTrainer).orElse(null);
        if (trainer == null)
            return new ResponseEntity<>("have no trainer with this id", HttpStatus.BAD_REQUEST);

        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.parse(date, formatterDate);
        String dayOfWeek = localDate.getDayOfWeek().toString().toLowerCase();

        Schedule schedule = trainer.getTrainerSchedule().scheduleList().stream()
                .filter(o -> o.getWeekDay().equals(dayOfWeek)).findFirst().orElse(null);

        List<Training> trainingListThisDayThisTrainer = trainer.getTraining().stream()
                .filter(o -> o.getDate().equals(localDate)).collect(Collectors.toList());

        if (schedule == null || schedule.getBegin() == null || schedule.getEnd() == null)
            return new ResponseEntity<>("trainer have no schedule in this day", HttpStatus.BAD_REQUEST);

        LocalTime begin = schedule.getBegin();
        LocalTime end = schedule.getEnd();

        ArrayList<LocalTime> localTimes = new ArrayList<>();
        for (LocalTime t = begin; t.plusMinutes(90).isBefore(end) || t.plusMinutes(90).equals(end); t = t.plusMinutes(30)) {
            if (this.countOverlaps(trainingListThisDayThisTrainer, t) < 3)
                localTimes.add(t);
        }
        return new ResponseEntity<>(localTimes, HttpStatus.OK);
    }

    private int countOverlaps(List<Training> trainings, LocalTime start) {
        LocalTime end = start.plusMinutes(90);
        int count = 0;
        for (Training training : trainings) {
            LocalTime trainingStart = training.getTime();
            LocalTime trainingEnd = trainingStart.plusMinutes(90);
            if (new LocalTimeRange(start, end).overlaps(new LocalTimeRange(trainingStart, trainingEnd)))
                count++;
        }
        return count;
    }
}
