package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TypesOfTasks;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        this(file, false);
    }

    public FileBackedTasksManager(String path) {
        this(new File(path));
    }

    public FileBackedTasksManager(File file, boolean isFile) {
        this.file = file;
        if (isFile) {
            load();
        }
    }

    private void load() { // Восстановление данных из файла
        try (final BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine(); // Пропустили первую строку с заголовками
            String line = reader.readLine();
            while (line != null) { // Почему нельзя использовать метод ready() ?
                if (!line.isBlank()) {
                    parseLineAndLoad(line);
                } else {
                    line = reader.readLine();
                    loadHistory(line);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e); // Зачем бросать свое исключение?
        }
    }

    private void parseLineAndLoad(String line) {
        if (line.startsWith("id") || line.isBlank()) {
            System.out.println("Ошибка. Неправильная строка.");
            return;
        }

        String[] elements = line.split(",");
        if (elements.length < 5) {
            System.out.println("Ошибка. Элементов в строке недостаточно");
            return;
        }

        int id = Integer.parseInt(elements[0]);
        TypesOfTasks type = TypesOfTasks.valueOf(elements[1]); //System.out.println("TYPE: " + type);
        String name = elements[2];
        Task.Status status = Task.Status.valueOf(elements[3]); //System.out.println("STATUS: " + status);
        String description = elements[4];

        switch (type) {
            case TASK:
                recreateTask(new Task(name, description, id, status));
                break;
            case EPIC:
                recreateEpic(new Epic(name, description, id));
                break;
            case SUBTASK:
                if (elements.length == 6) {
                    int idEpic = Integer.parseInt(elements[5]); //System.out.println("IDEPIC: " + idEpic);
                    Epic epic = epics.get(idEpic);
                    if (epic == null) {
                        System.out.println("Ошибка. Эпик == null");
                    }
                    recreateSubtask(new Subtask(name, description, id, status, epic));
                } else {
                    System.out.println("Ошибка. У подзадачи не найден id эпика.");
                }
                break;
            default:
                System.out.println("Ошибка. Не найдет тип задачи.");
        }
    }

    private void recreateTask(Task task) {
        final int id = task.getId();
        tasks.put(id, task);
        checkIdCounter(id);
    }

    private void recreateEpic(Epic epic) {
        final int id = epic.getId();
        epics.put(id, epic);
        checkIdCounter(id);
    }

    private void recreateSubtask(Subtask subtask) {
        final int id = subtask.getId();
        subtasks.put(id, subtask);
        Epic epic = subtask.getEpic();
        epic.addSubtask(subtask);
        setStatusOfEpic(epic);
        checkIdCounter(id);
    }

    private void checkIdCounter(int id) {
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
            } else {
                System.out.println("Ошибка. Не найдена задача по id.");
            }
        }
    }

    private void save() throws ManagerSaveException { // Сохранение данных в файл
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic\n");

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
            System.out.println("Ошибка. Не удалось создать writer или сделать запись в файл.");
            throw new ManagerSaveException(e);
        }
    }

    private String toString(Task task, TypesOfTasks type) {
        String result = task.getId() + "," + type + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription();
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
        List<Task> history = historyManager.getHistory(); //System.out.println("HISTORY: " + history);
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
        super.updateTask(updatedEpic);
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

    // Тесты
    public static void main(String[] args) {
        System.out.println("Пришло время практики!");

        // Создаем менеджер, но не загружаем данные из файла (имитация первого запуска программы)
        FileBackedTasksManager manager = new FileBackedTasksManager(new File("tasks.csv"));

        Task task11 = new Task("(taskName11)", "(description)");
        Epic epic11 = new Epic("(epicName11)", "(description)");
        Subtask subtask11 = new Subtask("(subtaskName11)", "(description)", epic11);
        Task task12 = new Task("(taskName12)", "(description)");

        manager.createTask(task11);
        manager.createEpic(epic11);
        manager.createSubtask(subtask11);
        manager.createTask(task12);

        manager.getTask(1);
        manager.getEpic(2);
        manager.getSubtask(3);

        // Создаем менеджер с загрузкой начальных данных из файла (имитация повторного запуска программы)
        FileBackedTasksManager managerFromFile = new FileBackedTasksManager(new File("tasks.csv"), true);

        // Проверяем идентичность первого менеджера и второго менеджера.
        // (второй менеджер получил данные из файла, созданного первым менеджером)
        manager.checkManagers(managerFromFile);

        // Дальшейшая работа второго менеджера (новые задачи)
        Task task21 = new Task("(taskName21)", "(description)");
        Epic epic21 = new Epic("(epicName21)", "(description)");
        Subtask subtask21 = new Subtask("(subtaskName21)", "(description)", epic21);
        Task task22 = new Task("(taskName22)", "(description)");

        managerFromFile.createTask(task21);
        managerFromFile.createEpic(epic21);
        managerFromFile.createSubtask(subtask21);
        managerFromFile.createTask(task22);

        managerFromFile.getTask(4);
        managerFromFile.getEpic(6);
        managerFromFile.getSubtask(3);

    } // end of main()

    private void checkManagers(FileBackedTasksManager managerFromFile) {

        for (Task task : tasks.values()) {
            int id = task.getId();
            Task taskFromFile = managerFromFile.tasks.get(id);
            compare(task, taskFromFile);
        }
        for (Task task : subtasks.values()) {
            int id = task.getId();
            Task taskFromFile = managerFromFile.subtasks.get(id);
            compare(task, taskFromFile);
        }
        for (Task task : epics.values()) {
            int id = task.getId();
            Task taskFromFile = managerFromFile.epics.get(id);
            compare(task, taskFromFile);
        }

        List<Task> history = historyManager.getHistory();
        List<Task> historyFromFile = managerFromFile.historyManager.getHistory();
        if (history.equals(historyFromFile)) {
            System.out.println("Истории сопадают.");
        } else {
            System.out.println("Ошибка. Истории не совпадают.");
        }
    }

    private void compare(Task task, Task taskFromFile) {
        if (task.equals(taskFromFile)) {
            System.out.println("TRUE: " + task.getName() + " equals " + taskFromFile.getName());
        } else {
            System.out.println("Ошибка. Задачи не равны");
        }
    }

}