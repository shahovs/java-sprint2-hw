// Тесты
package main;

import model.*;
import manager.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

/**
 *
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("*************************************************************************");
        System.out.println("Запускаем тесты класса Main");

        // Создаем менеджер, но не загружаем данные из файла (имитация первого запуска программы)
        FileBackedTaskManager manager = new FileBackedTaskManager(new File("tasks.csv"));

        Task task11 = new Task("(taskName1.1)", "(description)", 0, Task.Status.NEW,
                LocalDateTime.of(2022, 10, 1, 12, 0, 0), 10);
        Epic epic11 = new Epic("(epicName1.1)", "(description)");
        Subtask subtask11 = new Subtask("(subtaskName1.1)", "(description)", 0, Task.Status.NEW,
                epic11, LocalDateTime.of(2022, 2, 2, 12, 0, 0), 20);
        Task task12 = new Task("(taskName1.2)", "(description)");

        manager.createTask(task11);
        manager.createEpic(epic11);
        manager.createSubtask(subtask11);
        manager.createTask(task12);

        manager.getTask(1);
        manager.getEpic(2);
        manager.getSubtask(3);

        // Создаем менеджер с загрузкой начальных данных из файла (имитация повторного запуска программы)
        FileBackedTaskManager managerFromFile = new FileBackedTaskManager(new File("tasks.csv"), true);

        // Проверяем идентичность первого менеджера и второго менеджера.
        // (второй менеджер получил данные из файла, созданного первым менеджером)
        checkManagers(manager, managerFromFile);

        // Дальшейшая работа второго менеджера (новые задачи)
        Task task21 = new Task("(taskName2.1)", "(description)", 0, Task.Status.NEW,
                LocalDateTime.of(2022, 4, 4, 12, 0, 0), 10);
        Epic epic21 = new Epic("(epicName2.1)", "(description)");
        Subtask subtask21 = new Subtask("(subtaskName2.1)", "(description)", 0, Task.Status.NEW,
                epic21, LocalDateTime.of(2022, 1, 5, 12, 0, 0), 20);
        Task task22 = new Task("(taskName2.2)", "(description)", 0, Task.Status.NEW,
                LocalDateTime.of(2022, 4, 4, 12, 0, 0), 10);

        managerFromFile.createTask(task21);
        managerFromFile.createEpic(epic21);
        managerFromFile.createSubtask(subtask21);
        managerFromFile.createTask(task22);

        managerFromFile.getTask(4);
        managerFromFile.getEpic(6);
        managerFromFile.getSubtask(3);

        managerFromFile.updateTask(new Task("(updatedName1)", "(description)", 5, Task.Status.NEW,
                LocalDateTime.of(2022, 9, 1, 12, 0, 0), 10));
        managerFromFile.updateTask(new Task("(updatedName2)", "(description)", 4, Task.Status.NEW,
                LocalDateTime.of(2022, 12, 1, 12, 0, 0), 10));
        managerFromFile.updateTask(new Task("(updatedName3)", "(description)", 8, Task.Status.NEW));
        managerFromFile.updateTask(new Task("(updatedName4)", "(description)", 1, Task.Status.NEW,
                LocalDateTime.of(2022, 12, 1, 12, 0, 0), 10));


        TreeSet<Task> priorTasks = managerFromFile.getPrioritizedTasks();
        System.out.println("priorTask.size: " + priorTasks.size());
        for (Task task : priorTasks) {
            System.out.println(task.getStartTime() + " " + task.getDuration() + " " + task.getName());
        }

        System.out.println("Метод Main.main() завершен!");

    } // end of main()

    private static void checkManagers(FileBackedTaskManager manager, FileBackedTaskManager managerFromFile) {

        List<Task> tasksFromFile = managerFromFile.getAllTasks();
        List<Subtask> subtasksFromFile = managerFromFile.getAllSubtasks();
        List<Epic> epicsFromFile = managerFromFile.getAllEpics();

        for (Task task : manager.getAllTasks()) {
            int id = task.getId();
            Task taskFromFile = null;
            for (Task t : tasksFromFile) {
                if (t.getId() == id) {
                    taskFromFile = t;
                    break;
                }
            }
            compare(task, taskFromFile);
        }

        for (Subtask subtask : manager.getAllSubtasks()) {
            int id = subtask.getId();
            Task subtaskFromFile = null; //subtasksFromFile.get(id);
            for (Subtask st : subtasksFromFile) {
                if (st.getId() == id) {
                    subtaskFromFile = st;
                    break;
                }
            }
            compare(subtask, subtaskFromFile);
        }

        for (Epic epic : manager.getAllEpics()) {
            int id = epic.getId();
            Task epicFromFile = null; //epicsFromFile.get(id);
            for (Epic ep : epicsFromFile) {
                if (ep.getId() == id) {
                    epicFromFile = ep;
                    break;
                }
            }
            compare(epic, epicFromFile);
        }

        List<Task> history = manager.getHistory();
        List<Task> historyFromFile = managerFromFile.getHistory();
        if (!history.equals(historyFromFile)) {
            System.out.println("Истории не совпадают. Тест не пройден.");
        }
    }

    private static void compare(Task task, Task taskFromFile) {
        if (!task.equals(taskFromFile)) {
            System.out.println("Задачи не равны. Тест не пройден.");
        }
    }

}