package com.kopnyaev.fschool.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "trainers", uniqueConstraints = {@UniqueConstraint(columnNames = {"surname", "name", "patronymic"})})
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @Column(nullable = false)
    private String surname;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(nullable = false)
    private String patronymic;

    @NonNull
    @Column(nullable = false)
    private int experience;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "trainer")
    @PrimaryKeyJoinColumn
    private TrainerSchedule trainerSchedule;

    @OneToMany(mappedBy = "trainer")
    @Cascade(value = org.hibernate.annotations.CascadeType.DELETE)
    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Training> training;
}
