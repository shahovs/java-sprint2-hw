package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private int idCounter;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        idCounter = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            setStatusOfEpic(epic);
        }
        subtasks.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        //addToHistory(task);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        //addToHistory(epic);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        //addToHistory(subtask);
        historyManager.add(subtask);
        return subtask;
    }

/*    private void addToHistory(Task task) {
        history.add(task);
        if (history.size() > MAX_TASKS_IN_HISTORY) {
            history.remove(0);
        }
    }*/

    @Override
    public void createTask(Task task) {
        final int id = ++idCounter;
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void createEpic(Epic epic) {
        final int id = ++idCounter;
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        final int id = ++idCounter;
        subtask.setId(id);
        subtasks.put(id, subtask);
        Epic epic = subtask.getEpic();
        epic.addSubtask(subtask);
        setStatusOfEpic(epic);
    }

    @Override
    public void updateTask(Task updatedTask) {
        final int id = updatedTask.getId();
        if (!tasks.containsKey(id)) {
            return;
        }
        Task task = tasks.get(id);
        task.setName(updatedTask.getName());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        final int id = updatedEpic.getId();
        final Epic oldEpic = epics.get(id);
        if (oldEpic == null) {
            return;
        }
        oldEpic.setName(updatedEpic.getName());
        oldEpic.setDescription(updatedEpic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        final int id = updatedSubtask.getId();
        if (!subtasks.containsKey(id)) {
            return;
        }
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return;
        }
        subtask.setName(updatedSubtask.getName());
        subtask.setDescription(updatedSubtask.getDescription());
        subtask.setStatus(updatedSubtask.getStatus());
        Epic epic = subtask.getEpic();
        if (epic == null) {
            return;
        }
        setStatusOfEpic(epic);
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeEpic(int idEpic) {
        Epic epic = epics.get(idEpic);
        if (epic == null) {
            return;
        }
        for (Subtask subtask : epic.getSubtasks()) {
            int idSubtask = subtask.getId();
            subtasks.remove(idSubtask); // удаляем subtasks (относящихся к удаляемому epic) из HashMap класса manager.Manager
        }
        epic.removeAllSubtasks(); // удаляем subtasks (относящихся к удаляемому epic) из ArrayList класса model.Epic
        epics.remove(idEpic); // удаляем epic из HashMap класса manager.Manager
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        subtasks.remove(id); // удаляем из HashMap класса manager.Manager
        if (subtask == null) {
            return;
        }
        Epic epic = subtask.getEpic();
        if (epic == null) {
            return;
        }
        epic.removeSubtask(subtask); // удаляем из ArrayList класса model.Epic
        setStatusOfEpic(epic);
    }

    @Override
    public String toString() {
        String result = "TASKS:" + tasks.values();
        //result += "\n\nEPICS and subtasks:";
        for (Epic epic : epics.values()) {
            result += "\nEpic:" + epic + "\nEpic's subtasks:" + epic.getSubtasks();
        }
        return result;
    }

    private void setStatusOfEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        ArrayList<Subtask> subtasksOfEpic = epic.getSubtasks();
        if (subtasksOfEpic == null) {
            return;
        }
        if (subtasksOfEpic.size() == 0) {
            epic.setStatus(Task.Status.NEW);
            return;
        }
        Task.Status status = subtasksOfEpic.get(0).getStatus();
        epic.setStatus(status);
        for (Subtask subtask : subtasksOfEpic) {
            if (status != subtask.getStatus()) {
                epic.setStatus(Task.Status.IN_PROGRESS);
                return;
            }
        }
    }
}

