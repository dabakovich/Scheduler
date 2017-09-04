package com.dabakovich.entity;

import java.time.LocalDate;

/**
 * Created by dabak on 31.08.2017, 19:35.
 */
public class Schedule {

    private boolean active = true;
    private LocalDate startDate;
    private ScheduleType scheduleType = ScheduleType.CHRONOLOGICAL;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "active=" + active +
                ", startDate=" + startDate +
                ", scheduleType=" + scheduleType +
                '}';
    }
}
