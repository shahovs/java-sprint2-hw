package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import model.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;
    Task task;
    Epic epic;
    Subtask subtask;

    void initManager() {
        task = new Task("taskName", "d", 0, Task.Status.NEW,
                LocalDateTime.of(2022, 1, 1, 12, 0, 0), 5);
        epic = new Epic("epicName", "d", 0);
        subtask = new Subtask("subtaskName", "d", 0, Task.Status.NEW, epic,
                LocalDateTime.of(2022, 2, 2, 12, 0, 0), 15);
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask); //System.out.println("subtask id: " + subtask.getId());
    }

    @Test
    void getAllTasksTest() {
        final List<Task> tasks = manager.getAllTasks();
        assertNotNull(tasks);
        assertEquals(1, tasks.size(), "Одна задача");
        assertEquals(task, tasks.get(0), "Задача равна задаче из менеджера");
        assertEquals(1, tasks.get(0).getId(), "Id задачи равен 1");
    }

    @Test
    void getAllEpicsTest() {
        final List<Epic> epics = manager.getAllEpics();
        assertNotNull(epics);
        assertEquals(1, epics.size(), "Один эпик");
        assertEquals(epic, epics.get(0), "Эпик равен эпику из менеджера");
        assertEquals(2, epics.get(0).getId(), "Id эпика равен 2");
    }

    @Test
    void getAllSubtasksTest() {
        final List<Subtask> subtasks = manager.getAllSubtasks();
        assertNotNull(subtasks);
        assertEquals(1, subtasks.size(), "Одна подзадача");
        assertEquals(subtask, subtasks.get(0), "Подзадача равна подзадаче из менеджера");
        assertEquals(3, subtasks.get(0).getId(), "Id задачи равен 3");
    }

    @Test
    void removeAllTasks() {
        manager.removeAllTasks();
        assertEquals(0, manager.getAllTasks().size(), "задач нет");
    }

    @Test
    void removeAllEpics() {
        manager.removeAllEpics();
        assertEquals(0, manager.getAllEpics().size(), "эпиков нет");
        assertEquals(0, manager.getAllSubtasks().size(), "подзадач нет");
    }

    @Test
    void removeAllSubtasks() {
        manager.removeAllSubtasks();
        assertEquals(0, manager.getAllSubtasks().size(), "подзадач нет");
        assertEquals(0, epic.getSubtasks().size(), "подзадач в эпике нет");
    }

    @Test
    void getTask() {
        Task newTask = manager.getTask(1);
        assertNotNull(newTask);
        assertEquals(task, newTask);
    }

    @Test
    void getEpic() {
        Epic newEpic = manager.getEpic(2);
        assertNotNull(newEpic);
        assertEquals(epic, newEpic);
    }

    @Test
    void getSubtask() {
        Subtask newSubtask = manager.getSubtask(3);
        assertNotNull(newSubtask);
        assertEquals(subtask, newSubtask);
    }

    @Test
    void createTask() {
        Task newTask = new Task("taskName", "d", 0, Task.Status.NEW,
                LocalDateTime.now(), 5);
        manager.createTask(newTask);
        Task createdTask = manager.getTask(4);
        assertNotNull(createdTask, "Задача не равна null");
        assertEquals(newTask, createdTask, "Задача равна задаче из менеджера");
        assertEquals(2, manager.getAllTasks().size(), "Две задачи в менеджере");
    }

    @Test
    void createEpic() {
        Epic newEpic = new Epic("epicName", "d", 0);
        manager.createEpic(newEpic);
        Epic createdEpic = manager.getEpic(4);
        assertNotNull(createdEpic, "Эпик не равен null");
        assertEquals(newEpic, createdEpic, "Эпик равен эпику из менеджера");
        assertEquals(2, manager.getAllEpics().size(), "Два эпика в менеджере");
    }

    @Test
    void createSubtask() {
        Subtask newSubtask = new Subtask("subtaskName", "d", 0, Task.Status.NEW, epic,
                LocalDateTime.now(), 15);
        manager.createSubtask(newSubtask);
        Subtask createdSubtask = manager.getSubtask(4);
        assertNotNull(createdSubtask, "Подзадача не равна null");
        assertEquals(newSubtask, createdSubtask, "Подзадача равна подзадаче из менеджера");
        assertEquals(2, manager.getAllSubtasks().size(), "Две подзадачи в менеджере");
        assertEquals(2, epic.getSubtasks().size(), "Две подзадачи в эпике");
    }

    @Test
    void updateTask() {
        Task newTask = new Task("newName", "newD", 1, Task.Status.IN_PROGRESS,
                LocalDateTime.now(), 30);
        manager.updateTask(newTask);
        Task updatedTask = manager.getTask(1);
        assertNotNull(updatedTask);
        assertEquals("newName", updatedTask.getName(), "имя обновлено");
        assertEquals("newD", updatedTask.getDescription(), "описание обновлено");
        assertEquals(Task.Status.IN_PROGRESS, updatedTask.getStatus(), "статус обновлен");
    }

    @Test
    void updateEpic() {
        Epic newEpic = new Epic("newName", "newD", 2);
        manager.updateEpic(newEpic);
        Epic updatedEpic = manager.getEpic(2);
        assertNotNull(updatedEpic);
        assertEquals("newName", updatedEpic.getName(), "имя обновлено");
        assertEquals("newD", updatedEpic.getDescription(), "описание обновлено");
    }

    @Test
    void updateSubtask() {
        Subtask newSubtask = new Subtask("newName", "newD", 3, Task.Status.IN_PROGRESS, epic,
                LocalDateTime.now(), 30);
        manager.updateSubtask(newSubtask);
        Subtask updatedSubtask = manager.getSubtask(3);
        assertNotNull(updatedSubtask);
        assertEquals("newName", updatedSubtask.getName(), "имя обновлено");
        assertEquals("newD", updatedSubtask.getDescription(), "описание обновлено");
        assertEquals(Task.Status.IN_PROGRESS, updatedSubtask.getStatus(), "статус обновлен");
    }

    @Test
    void removeTask() {
        manager.removeTask(1);
        assertEquals(0, manager.getAllTasks().size(), "задача удалена");
    }

    @Test
    void removeEpic() {
        manager.removeEpic(2);
        assertEquals(0, manager.getAllEpics().size(), "эпик удален");
        assertEquals(0, manager.getAllSubtasks().size(), "подзадача эпика удалена");
    }

    @Test
    void removeSubtask() {
        manager.removeSubtask(3);
        assertEquals(0, manager.getAllSubtasks().size(), "подзадача удалена");
    }

    @Test
    void epicWithEmptySubtasks() {
        InMemoryTaskManager managerMemory = new InMemoryTaskManager();
        Epic epic1 = new Epic("epicName", "d", 0);
        managerMemory.createEpic(epic1);
        managerMemory.setStatusOfEpic(epic1);
        assertEquals(Task.Status.NEW, epic1.getStatus(), "");
    }

    @Test
    void epicWithNewSubtasks() {
        InMemoryTaskManager managerMemory = new InMemoryTaskManager();
        Epic epic1 = new Epic("epicName", "d", 0);
        managerMemory.createEpic(epic1);
        managerMemory.createSubtask(new Subtask("subtaskName1", "d", 0, Task.Status.NEW, epic1));
        managerMemory.createSubtask(new Subtask("subtaskName2", "d", 0, Task.Status.NEW, epic1));
        managerMemory.setStatusOfEpic(epic1);
        assertEquals(Task.Status.NEW, epic1.getStatus(), "");
    }

    @Test
    void epicWithNewAndDoneSubtasks() {
        InMemoryTaskManager managerMemory = new InMemoryTaskManager();
        Epic epic1 = new Epic("epicName", "d", 0);
        managerMemory.createEpic(epic1);
        managerMemory.createSubtask(new Subtask("subtaskName1", "d", 0, Task.Status.NEW, epic1));
        managerMemory.createSubtask(new Subtask("subtaskName2", "d", 0, Task.Status.DONE, epic1));
        managerMemory.setStatusOfEpic(epic1);
        assertEquals(Task.Status.IN_PROGRESS, epic1.getStatus(), "");
    }

    @Test
    void epicWithInProgressSubtasks() {
        InMemoryTaskManager managerMemory = new InMemoryTaskManager();
        Epic epic1 = new Epic("epicName", "d", 0);
        managerMemory.createEpic(epic1);
        managerMemory.createSubtask(new Subtask("subtaskName1", "d", 0, Task.Status.IN_PROGRESS, epic1));
        managerMemory.createSubtask(new Subtask("subtaskName2", "d", 0, Task.Status.IN_PROGRESS, epic1));
        managerMemory.setStatusOfEpic(epic1);
        assertEquals(Task.Status.IN_PROGRESS, epic1.getStatus(), "");
    }

    @Test
    void epicWithDoneSubtasks() {
        InMemoryTaskManager managerMemory = new InMemoryTaskManager();
        Epic epic1 = new Epic("epicName", "d", 0);
        managerMemory.createEpic(epic1);
        managerMemory.createSubtask(new Subtask("subtaskName1", "d", 0, Task.Status.DONE, epic1));
        managerMemory.createSubtask(new Subtask("subtaskName2", "d", 0, Task.Status.DONE, epic1));
        managerMemory.setStatusOfEpic(epic1);
        assertEquals(Task.Status.DONE, epic1.getStatus(), "");
    }
}