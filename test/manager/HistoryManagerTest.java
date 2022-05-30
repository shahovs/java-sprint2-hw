package manager;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    InMemoryTaskManager manager;

    @BeforeEach
    void init() {
        manager = new InMemoryTaskManager();
        manager.createTask(new Task("task1", "d", 0, Task.Status.NEW));
        manager.createTask(new Task("task2", "d", 0, Task.Status.NEW));
        manager.createTask(new Task("task3", "d", 0, Task.Status.NEW));
        manager.getTask(1);
        manager.getTask(2);
        manager.getTask(1);
        manager.getTask(3);
    }

    @Test
    void add() {
        List<Task> history = manager.getHistory();
        assertEquals("task2", history.get(0).getName());
        assertEquals("task1", history.get(1).getName());
        assertEquals("task3", history.get(2).getName());
    }

    @Test
    void getHistory() {
        List<Task> history = manager.getHistory();
        assertEquals(3, history.size());
    }

    @Test
    void remove() {
        manager.createTask(new Task("task4", "d", 0, Task.Status.NEW));
        manager.createTask(new Task("task5", "d", 0, Task.Status.NEW));
        manager.createTask(new Task("task6", "d", 0, Task.Status.NEW));
        manager.getTask(1);
        manager.getTask(2);
        manager.getTask(3);
        manager.getTask(4);
        manager.getTask(5);
        manager.getTask(6);

        manager.historyManager.remove(1);
        assertEquals(5, manager.getHistory().size());
        assertEquals("task2", manager.getHistory().get(0).getName());
        manager.historyManager.remove(1);
        assertEquals(5, manager.getHistory().size());

        manager.historyManager.remove(6);
        assertEquals(4, manager.getHistory().size());
        assertEquals("task5", manager.getHistory().get(3).getName());


        manager.historyManager.remove(999);
        assertEquals(4, manager.getHistory().size());

        manager.historyManager.remove(3);
        assertEquals(3, manager.getHistory().size());
        assertEquals("task2", manager.getHistory().get(0).getName());
        assertEquals("task4", manager.getHistory().get(1).getName());

        manager.historyManager.remove(2);
        manager.historyManager.remove(4);
        manager.historyManager.remove(5);
        assertEquals(0, manager.getHistory().size());
    }
}