package com.kopnyaev.fschool.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class Schedule {
    private String weekDay;
    private LocalTime begin;
    private LocalTime end;
}
