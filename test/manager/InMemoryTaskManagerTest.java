package manager;

import main.Main;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeAll
    static void checkMain() {
        Main.main(null); // Запускаем тесты из Main.main(), чтобы не пропадали зря ))
    }

    @BeforeEach
    void initInMemoryTaskManager() {
        manager = new InMemoryTaskManager();
        initManager();
    }

    @Test
    void shouldHaveEmptyFields() {
        InMemoryTaskManager managerMemory = new InMemoryTaskManager();
        assertEquals(0, managerMemory.getAllTasks().size(), "Задач нет");
        assertEquals(0, managerMemory.getAllEpics().size(), "Эпиков нет");
        assertEquals(0, managerMemory.getAllSubtasks().size(), "Подзадач нет");
        assertEquals(0, managerMemory.getHistory().size(), "Истории нет");
    }
}