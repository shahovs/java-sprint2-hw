// Ульяна, спасибо за поддержку! Это очень важно :)
import java.util.HashMap;
import java.util.ArrayList;

public class Manager {
    private int idCounter;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;

    public Manager() {
        idCounter = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            setStatusOfEpic(epic);
        }
        subtasks.clear();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public void createTask(Task task) {
        final int id = ++idCounter;
        task.setId(id);
        tasks.put(id, task);
    }

    public void createEpic(Epic epic) {
        final int id = ++idCounter;
        epic.setId(id);
        epics.put(id, epic);
    }

    public void createSubtask(Subtask subtask) {
        final int id = ++idCounter;
        subtask.setId(id);
        subtasks.put(id, subtask);
        Epic epic = subtask.getEpic();
        epic.addSubtask(subtask);
        setStatusOfEpic(epic);
    }

    public void updateTask(Task updatedTask) {
        int id = updatedTask.getId();
        if (!tasks.containsKey(id)) {
            return;
        }
        tasks.put(id, updatedTask);
    }

    public void updateEpic(Epic updatedEpic) {
        int id = updatedEpic.getId();
        if (!epics.containsKey(id)) {
            return;
        }
        Epic removingEpic = epics.get(id);
        ArrayList<Subtask> subtasksOfRemovingEpic = removingEpic.getSubtasks();
        updatedEpic.updateSubtasks(subtasksOfRemovingEpic);
        Task.Status status = removingEpic.getStatus();
        updatedEpic.setStatus(status);
        epics.put(id, updatedEpic);
    }

    public void updateSubtask(Subtask updatedSubtask) {
        int id = updatedSubtask.getId();
        if (!subtasks.containsKey(id)) {
            return;
        }
        removeSubtask(id);
        Epic epic = epics.get(updatedSubtask.getEpic().getId());
        epic.addSubtask(updatedSubtask);
        subtasks.put(id, updatedSubtask);
        setStatusOfEpic(epic);
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeEpic(int idEpic) {
        Epic epic = epics.get(idEpic);
        if (epic == null) {
            return;
        }
        for (Subtask subtask : epic.getSubtasks()) {
            int idSubtask = subtask.getId();
            subtasks.remove(idSubtask); // удаляем subtasks (относящихся к удаляемому epic) из HashMap класса Manager
        }
        epic.removeAllSubtasks(); // удаляем subtasks (относящихся к удаляемому epic) из ArrayList класса Epic
        epics.remove(idEpic); // удаляем epic из HashMap класса Manager
    }

    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = subtask.getEpic();
        epic.removeSubtask(subtask); // удаляем из ArrayList класса Epic
        subtasks.remove(id); // удаляем из HashMap класса Manager
        setStatusOfEpic(epic);
    }

    public ArrayList<Subtask> getSubtasksOfEpic(int id) {
        return epics.get(id).getSubtasks();
    }

    @Override
    public String toString() {
        String result = "TASKS:\n" + tasks;
        result += "\nEPICS and subtasks:";
        for (Epic epic : epics.values()) {
            result += "\n" + epic + "\n" + epic.getSubtasks();
        }
        return result;
    }

    private void setStatusOfEpic(Epic epic) {
        ArrayList<Subtask> subtasksOfEpic = epic.getSubtasks();
        if (subtasksOfEpic.size() == 0) {
            epic.setStatus(Task.Status.NEW);
            return;
        }
        Task.Status status = subtasksOfEpic.get(0).getStatus();
        for (Subtask subtask : subtasksOfEpic) {
            if (status != subtask.getStatus()) {
                epic.setStatus(Task.Status.IN_PROGRESS);
                return;
            }
        }
        epic.setStatus(status);
    }
}

