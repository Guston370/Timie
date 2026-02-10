package com.mit.timie.model;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private int workingDays;
    private List<String> dayNames;
    private int periodsPerDay;
    private int periodDuration;
    private List<Integer> breakPeriods;

    public Config() {
        this.dayNames = new ArrayList<>();
        this.breakPeriods = new ArrayList<>();
    }

    public Config(int workingDays, List<String> dayNames, int periodsPerDay, int periodDuration, List<Integer> breakPeriods) {
        this.workingDays = workingDays;
        this.dayNames = dayNames != null ? new ArrayList<>(dayNames) : new ArrayList<>();
        this.periodsPerDay = periodsPerDay;
        this.periodDuration = periodDuration;
        this.breakPeriods = breakPeriods != null ? new ArrayList<>(breakPeriods) : new ArrayList<>();
    }

    public int getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(int workingDays) {
        this.workingDays = workingDays;
    }

    public List<String> getDayNames() {
        return dayNames;
    }

    public void setDayNames(List<String> dayNames) {
        this.dayNames = dayNames != null ? new ArrayList<>(dayNames) : new ArrayList<>();
    }

    public int getPeriodsPerDay() {
        return periodsPerDay;
    }

    public void setPeriodsPerDay(int periodsPerDay) {
        this.periodsPerDay = periodsPerDay;
    }

    public int getPeriodDuration() {
        return periodDuration;
    }

    public void setPeriodDuration(int periodDuration) {
        this.periodDuration = periodDuration;
    }

    public List<Integer> getBreakPeriods() {
        return breakPeriods;
    }

    public void setBreakPeriods(List<Integer> breakPeriods) {
        this.breakPeriods = breakPeriods != null ? new ArrayList<>(breakPeriods) : new ArrayList<>();
    }

    public boolean validate() {
        if (workingDays < 1 || workingDays > 7) {
            return false;
        }
        if (periodsPerDay < 1 || periodsPerDay > 12) {
            return false;
        }
        if (periodDuration <= 0) {
            return false;
        }
        if (dayNames == null || dayNames.size() != workingDays) {
            return false;
        }
        if (!hasAtLeastOneTeachingPeriodPerDay()) {
            return false;
        }
        return true;
    }

    public boolean hasAtLeastOneTeachingPeriodPerDay() {
        if (breakPeriods == null) {
            return periodsPerDay > 0;
        }
        int teachingPeriods = periodsPerDay - breakPeriods.size();
        return teachingPeriods > 0;
    }

    public String getValidationError() {
        if (workingDays < 1 || workingDays > 7) {
            return "Working days must be between 1 and 7";
        }
        if (periodsPerDay < 1 || periodsPerDay > 12) {
            return "Periods per day must be between 1 and 12";
        }
        if (periodDuration <= 0) {
            return "Period duration must be greater than 0";
        }
        if (dayNames == null || dayNames.size() != workingDays) {
            return "Day names must match the number of working days";
        }
        if (!hasAtLeastOneTeachingPeriodPerDay()) {
            return "At least one teaching period must exist per day";
        }
        return null;
    }
}
