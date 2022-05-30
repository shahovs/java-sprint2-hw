package model;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description, int id, Status status, Epic epic,
                   LocalDateTime startTime, int duration) {
        super(name, description, id, status, startTime, duration);
        this.epic = epic;
    }

    public Subtask(String name, String description, Epic epic) {
        this(name, description, 0, Task.Status.NEW, epic);
    }

    public Subtask(String name, String description, int id, Status status, Epic epic) {
        this(name, description, id, status, epic, null, 0);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public TypesOfTasks getType() {
        return TypesOfTasks.SUBTASK;
    }

}
