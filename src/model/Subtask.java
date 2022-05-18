package model;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description, int id, Status status, Epic epic) {
        super(name, description, id, status);
        this.epic = epic;
    }

    public Subtask(String name, String description, Epic epic) {
        this(name, description, 0, Task.Status.NEW, epic);
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
