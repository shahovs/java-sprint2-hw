package manager;

import exception.ManagerException;
import exception.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TypesOfTasks;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    final static int MIN_ELEMENTS_IN_RECORD = 8;
    final static int MAX_ELEMENTS_IN_RECORD = 9;
    final static int NUMBER_OF_INDEX_OF_EPIC_IN_RECORD = 8;
    final static String STRING_IF_DATE_TIME_NULL = " ";
    // Запятые в форматтере запрещены (из-за использования файлов формата csv)
    final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLL yyyy HH:mm");

    private final File file;

    public FileBackedTaskManager(String path) {
        this(new File(path));
    }

    public FileBackedTaskManager(File file) {
        this(file, false);
    }

    public FileBackedTaskManager(File file, boolean isFile) {
        this.file = file;
        if (isFile) {
            load();
        }
    }

    protected void load() { // Восстановление данных из файла
        try (final BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine(); // Пропустили первую строку с заголовками
            String line = reader.readLine();
            while (line != null) {
                if (!line.isBlank()) {
                    try {
                        parseLineAndLoad(line);
                    } catch (Throwable e) {

                    }
                } else {
                    line = reader.readLine();
                    loadHistory(line);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private void parseLineAndLoad(String line) throws ManagerException {
        if (line.startsWith("id") || line.isBlank()) {
            return;
        }

        String[] elements = line.split(",");
        if (elements.length < MIN_ELEMENTS_IN_RECORD) {
            throw new ManagerException(new Throwable());
        }

        int id = Integer.parseInt(elements[0]);
        TypesOfTasks type = TypesOfTasks.valueOf(elements[1]);
        String name = elements[2];
        Task.Status status = Task.Status.valueOf(elements[3]);
        String description = elements[4];
        LocalDateTime startTime = null;
        if (!STRING_IF_DATE_TIME_NULL.equals(elements[5])) {
            startTime = LocalDateTime.parse(elements[5], formatter);
        }
        int duration = Integer.parseInt(elements[6]);

        switch (type) {
            case TASK:
                Task t = new Task(name, description, id, status, startTime, duration);
                tasks.put(id, t);
                prioritizedTasks.add(t);
                break;
            case EPIC:
                epics.put(id, new Epic(name, description, id));
                break;
            case SUBTASK:
                if (elements.length == MAX_ELEMENTS_IN_RECORD) {
                    int idEpic = Integer.parseInt(elements[NUMBER_OF_INDEX_OF_EPIC_IN_RECORD]);
                    Epic epic = epics.get(idEpic);
                    Subtask subtask = new Subtask(name, description, id, status, epic, startTime, duration);
                    subtasks.put(id, subtask);
                    prioritizedTasks.add(subtask);
                    epic.addSubtask(subtask);
                    epic.setStatusOfEpic();
                }
        }
        if (idCounter < id) {
            idCounter = id;
        }
    }

    private void loadHistory(String line) {
        if (line == null || line.isBlank()) {
            return;
        }
        String[] integers = line.split(",");
        for (String integer : integers) {
            final int id = Integer.parseInt(integer);
            if (tasks.containsKey(id)) {
                historyManager.add(tasks.get(id));
            } else if (epics.containsKey(id)) {
                historyManager.add(epics.get(id));
            } else if (subtasks.containsKey(id)) {
                historyManager.add(subtasks.get(id));
            }
        }
    }

    protected void save() throws ManagerSaveException { // Сохранение данных в файл
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,start,duration,finish,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(toString(task, TypesOfTasks.TASK));
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic, TypesOfTasks.EPIC));
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask, TypesOfTasks.SUBTASK));
            }

            writer.write(makeHistoryString());

        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private String toString(Task task, TypesOfTasks type) {
        String startTime = STRING_IF_DATE_TIME_NULL;
        String finishTime = STRING_IF_DATE_TIME_NULL;
        if (task.getStartTime() != null) {
            startTime = task.getStartTime().format(formatter);
        }
        if (task.getFinishTime() != null) {
            finishTime = task.getFinishTime().format(formatter);
        }
        String result = task.getId() + "," + type + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + "," + startTime + "," + task.getDuration() + "," + finishTime;
        if (TypesOfTasks.SUBTASK.equals(type)) {
            Subtask subtask = (Subtask) task;
            final int idEpic = subtask.getEpic().getId();
            result += "," + idEpic;
        }
        result += "\n";
        return result;
    }

    private String makeHistoryString() {
        StringBuilder result = new StringBuilder("\n");
        List<Task> history = historyManager.getHistory();
        int lastIndex = history.size() - 1;
        for (int index = 0; index < history.size(); index++) {
            result.append(history.get(index).getId());
            if (index != lastIndex) {
                result.append(",");
            }
        }
        return result.toString();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int idEpic) {
        super.removeEpic(idEpic);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

}