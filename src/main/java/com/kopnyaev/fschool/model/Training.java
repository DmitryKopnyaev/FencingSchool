package com.kopnyaev.fschool.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "trainings",uniqueConstraints = {@UniqueConstraint(columnNames = {"apprentice_id", "date"})}) //Ученик в один день не может присутствовать сразу на нескольких тренировках.
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @Column(nullable = false)
    private int numberGym;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Trainer trainer;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Apprentice apprentice;

    @NonNull
    @Column(nullable = false)
    private LocalDate date;

    @NonNull
    @Column(nullable = false)
    private LocalTime time;

    public Training(@NonNull int numberGym, Trainer trainer, Apprentice apprentice, @NonNull LocalDate date, @NonNull LocalTime time) {
        this.numberGym = numberGym;
        this.trainer = trainer;
        this.apprentice = apprentice;
        this.date = date;
        this.time = time;
    }
}
