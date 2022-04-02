import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(String title, String description, int id) {
        super(title, description, id, Task.Status.NEW);
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

    @Override
    public String toString() {
        String result = super.toString();
        result += "\nSUBTASKS: " + subtasks;
        return result;
    }
}
