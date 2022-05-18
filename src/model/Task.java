package model;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;

    public enum Status {
        NEW,
        DONE,
        IN_PROGRESS
    }

    private Status status;

    public Task(String name, String description, int id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(String name, String description) {
        this(name, description, 0, Status.NEW);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && id == task.id && Objects.equals(status, task.status);
    }
}




















