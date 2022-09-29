package com.kopnyaev.fschool.util;

import java.time.LocalTime;

public class LocalTimeRange {
    private final LocalTime from;
    private final LocalTime to;

    public LocalTimeRange(LocalTime from, LocalTime to) {
        this.from = from;
        this.to = to;
    }

    public boolean overlaps(LocalTimeRange other) {
        return this.from.equals(other.from)
                || isBetween(other.from, this.from, this.to) || isBetween(other.to, this.from, this.to)
                || isBetween(this.from, other.from, other.to) || isBetween(this.to, other.from, other.to);
    }

    public static boolean isBetween(LocalTime t, LocalTime from, LocalTime to) {
        if (from.isBefore(to)) { // same day
            return from.isBefore(t) && t.isBefore(to);
        } else { // spans to the next day.
            return from.isBefore(t) || t.isBefore(to);
        }
    }
}
