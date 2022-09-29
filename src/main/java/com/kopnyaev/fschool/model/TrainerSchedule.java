package com.kopnyaev.fschool.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "trainer_chedules")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class TrainerSchedule {
    @Id
    @Column(name = "trainer_id")
    private long id;

    @NonNull
    @ToString.Exclude
    @OneToOne
    @MapsId
    @JoinColumn(name = "trainer_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Trainer trainer;

    private LocalTime mondayStart;

    private LocalTime mondayEnd;

    private LocalTime tuesdayStart;

    private LocalTime tuesdayEnd;

    private LocalTime wednesdayStart;

    private LocalTime wednesdayEnd;

    private LocalTime thursdayStart;

    private LocalTime thursdayEnd;

    private LocalTime fridayStart;

    private LocalTime fridayEnd;

    private LocalTime saturdayStart;

    private LocalTime saturdayEnd;

    private LocalTime sundayStart;

    private LocalTime sundayEnd;

    public List<Schedule> scheduleList() {
        String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        List<Schedule> list = new ArrayList<>();
        for (String day : days) {
            try {
                LocalTime dayStart = (LocalTime) this.getClass().getDeclaredField(day + "Start").get(this);
                LocalTime dayEnd = (LocalTime) this.getClass().getDeclaredField(day + "End").get(this);
                list.add(new Schedule(day, dayStart, dayEnd));
            } catch (IllegalAccessException | NoSuchFieldException ignored) {
            }
        }
        return list;
    }

    public void addSchedule(String weekDay, String start, String end) {
        weekDay = weekDay.toLowerCase();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            this.getClass().getDeclaredField(weekDay + "Start").set(this, LocalTime.parse(start, formatter));
            this.getClass().getDeclaredField(weekDay + "End").set(this, LocalTime.parse(end, formatter));
        } catch (IllegalAccessException ignored) {
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("No such day of week");
        }
    }

    public void deleteSchedule(String weekDay) {
        weekDay = weekDay.toLowerCase();
        try {
            this.getClass().getDeclaredField(weekDay + "Start").set(this, null);
            this.getClass().getDeclaredField(weekDay + "End").set(this, null);
        } catch (IllegalAccessException | NoSuchFieldException ignored) {
        }
    }
}
