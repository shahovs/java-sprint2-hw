package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    void initFileBackedTaskManager() {
        manager = new FileBackedTaskManager("tasks.csv");
        initManager();
    }

    @Test
    void FileBackedTaskManagerTest() {
        FileBackedTaskManager managerFileBacked = new FileBackedTaskManager("tasks.csv");
        assertEquals(0, managerFileBacked.getAllTasks().size(), "Задач нет");
        assertEquals(0, managerFileBacked.getAllEpics().size(), "Эпиков нет");
        assertEquals(0, managerFileBacked.getAllSubtasks().size(), "Подзадач нет");
        assertEquals(0, managerFileBacked.getHistory().size(), "Истории нет");
    }
}