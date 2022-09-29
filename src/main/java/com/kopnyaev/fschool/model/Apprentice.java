package com.kopnyaev.fschool.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "apprentices")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Apprentice {
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
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @OneToMany(mappedBy = "apprentice", cascade = CascadeType.ALL)
    @Cascade(value = org.hibernate.annotations.CascadeType.DELETE)
    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Training> trainings;
}
