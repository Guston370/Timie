package com.mit.timie.model;

import java.util.Objects;

public class TimeSlot {
    private int day;
    private int period;

    public TimeSlot() {
    }

    public TimeSlot(int day, int period) {
        this.day = day;
        this.period = period;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return day == timeSlot.day && period == timeSlot.period;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, period);
    }

    @Override
    public String toString() {
        return "TimeSlot{day=" + day + ", period=" + period + "}";
    }
}
