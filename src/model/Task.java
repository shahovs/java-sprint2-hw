package model;

import java.util.Objects;
import java.time.LocalDateTime;

public class Task {
    private String name;
    private String description;
    private int id;
    Status status;
    LocalDateTime startTime;
    int duration;
    LocalDateTime finishTime;

    public enum Status {
        NEW, DONE, IN_PROGRESS
    }

    public Task(String name, String description, int id, Status status, LocalDateTime startTime, int duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.finishTime = (startTime != null) ? startTime.plusMinutes(duration) : null;
    }

    public Task(String name, String description) {
        this(name, description, 0, Status.NEW);
    }

    public Task(String name, String description, int id, Status status) {
        this(name, description, id, status, null, 0);
    }

    private LocalDateTime getEndTime(LocalDateTime startTime, int duration) {
        return startTime.plusMinutes(duration);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        calculateFinishTime();
    }

    public void setDuration(int duration) {
        this.duration = duration;
        calculateFinishTime();
    }

    private void calculateFinishTime() {
        if (startTime == null) {
            return;
        }
        finishTime = startTime.plusMinutes(duration);
    }

    public TypesOfTasks getType() {
        return TypesOfTasks.TASK;
    }

    @Override
    public String toString() {
        String result = "\nId " + id + " Name: '" + name + "' Description: '" + description + "' Status: " + status;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Task task = (Task) obj;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && id == task.id && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

}