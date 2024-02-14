package manager;

import model.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {

    protected int idCounter;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Subtask> subtasks;
    protected HistoryManager historyManager;
    protected TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        idCounter = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = new InMemoryHistoryManager();
        prioritizedTasks = new TreeSet<>(
                (Task t1, Task t2) -> {
                    if (t1.getStartTime() == null) return 1;
                    if (t2.getStartTime() == null) return -1;
                    return t1.getStartTime().compareTo(t2.getStartTime());
                }
        );
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
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
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        }
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        }
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            epic.setStatusOfEpic();
        }
        subtasks.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public void createTask(Task task) {
        if (!checkTimeAvailable(task)) {
            return;
        }
        final int id = ++idCounter;
        task.setId(id);
        tasks.put(id, task);
        prioritizedTasks.add(task);
    }

    @Override
    public void createEpic(Epic epic) {
        final int id = ++idCounter;
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (!checkTimeAvailable(subtask)) {
            return;
        }
        final int id = ++idCounter;
        subtask.setId(id);
        subtasks.put(id, subtask);
        int idEpic = subtask.getEpic().getId();
        Epic epic = epics.get(idEpic);
        subtask.setEpic(epic);
        epic.addSubtask(subtask);
        epic.setStatusOfEpic();
        prioritizedTasks.add(subtask);
    }

    private boolean checkTimeAvailable(Task newTask) {
        LocalDateTime startTime = newTask.getStartTime();
        LocalDateTime finishTime = newTask.getFinishTime();
        // Если у нас задача без времени либо prioritizedTasks еще пустой, то true
        if (startTime == null || prioritizedTasks.isEmpty()) {
            return true;
        }
        // Если уже есть задача, которая начинается в тот же момент, что и наша задача.
        if (prioritizedTasks.contains(newTask)) {
            return false;
        }

        Task previous = null;
        for (Task nextTask : prioritizedTasks) {
            // Если задачи со временем закончились и начались задачи без времени, то цикл завершаем
            // (наша задача оказалась последней либо все задачи были без времени)
            if (nextTask.getStartTime() == null) {
                break;
            }
            if (startTime.isBefore(nextTask.getStartTime())) {
                // Если обнаружили задачу, которая начинается после начала нашей
                // и при этом наша задача заканчивается позже, чем начинается найденная, то false
                if (finishTime.isAfter(nextTask.getStartTime())) {
                    return false;
                }
                // Если наша задача оказалась первой в списке и заканчивается до начала следующей, то true
                if (previous == null) {
                    return true;
                }
                // Если задача перед нашей заказчивается после начала нашей, то false
                if (previous.getFinishTime().isAfter(startTime)) {
                    return false;
                } else {
                    return true;
                }
            }
            previous = nextTask;
        }
        // Если все задачи были без времени, то true
        if (previous == null) {
            return true;
        }
        // Если наша задача оказалась последней, то проверяем, что предыдущая задача заказчивается до начала нашей
        if (startTime.isAfter(previous.getFinishTime())) {
            return true;
        }
        return false;
    }

    @Override
    public void updateTask(Task updatedTask) {
        if (updatedTask == null) {
            return;
        }
        final int id = updatedTask.getId();
        Task task = tasks.get(id);
        if (task == null) {
            return;
        }

        task.setName(updatedTask.getName());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());

        prioritizedTasks.remove(task);
        if (checkTimeAvailable(updatedTask)) {
            task.setStartTimeDurationAndCalculateFinish(updatedTask.getStartTime(), updatedTask.getDuration());
        } else {
            System.out.println("Извините. Новое время для выполнения задачи уже занято. " +
                    "Время выполнения задачи обновлено не будет.");
        }
        prioritizedTasks.add(task);
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        if (updatedEpic == null) {
            return;
        }
        final int id = updatedEpic.getId();
        Epic oldEpic = epics.get(id);
        if (oldEpic == null) {
            return;
        }
        oldEpic.setName(updatedEpic.getName());
        oldEpic.setDescription(updatedEpic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        if (updatedSubtask == null) {
            return;
        }
        final int id = updatedSubtask.getId();
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return;
        }
        subtask.setName(updatedSubtask.getName());
        subtask.setDescription(updatedSubtask.getDescription());
        subtask.setStatus(updatedSubtask.getStatus());

        prioritizedTasks.remove(subtask);
        if (checkTimeAvailable(updatedSubtask)) {
            subtask.setStartTimeDurationAndCalculateFinish(updatedSubtask.getStartTime(), updatedSubtask.getDuration());
        } else {
            System.out.println("Извините. Новое время для выполнения подзадачи уже занято. " +
                    "Время выполнения подзадачи обновлено не будет.");
        }
        prioritizedTasks.add(subtask);

        Epic epic = subtask.getEpic();
        if (epic == null) {
            return;
        }
        epic.setStatusOfEpic();
    }

    @Override
    public void removeTask(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int idEpic) {
        Epic epic = epics.get(idEpic);
        if (epic == null) {
            return;
        }
        for (Subtask subtask : epic.getSubtasks()) {
            int idSubtask = subtask.getId();
            prioritizedTasks.remove(subtask);
            subtasks.remove(idSubtask); // удаляем subtasks (относящихся к удаляемому epic) из HashMap класса manager.Manager
            historyManager.remove(idSubtask);
        }
        epic.removeAllSubtasks(); // удаляем subtasks (относящихся к удаляемому epic) из ArrayList класса model.Epic
        epics.remove(idEpic); // удаляем epic из HashMap класса manager.Manager
        historyManager.remove(idEpic);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return;
        }
        prioritizedTasks.remove(subtask);
        subtasks.remove(id); // удаляем из HashMap класса manager.Manager
        Epic epic = subtask.getEpic();
        if (epic == null) {
            return;
        }
        epic.removeSubtask(subtask); // удаляем из ArrayList класса model.Epic
        epic.setStatusOfEpic();
        historyManager.remove(id);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("TASKS:" + tasks.values());
        for (Epic epic : epics.values()) {
            result.append("\nEpic:").append(epic).append("\nEpic's subtasks:").append(epic.getSubtasks());
        }
        return result.toString();
    }

}

