import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description, int id) {
        super(name, description, id, Task.Status.NEW);
        subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void updateSubtasks(ArrayList<Subtask> updatedSubtasks) {
        subtasks = updatedSubtasks;
    }
}
