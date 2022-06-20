package manager;

import http.KVServer;
import model.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    KVServer kvServer;
    @BeforeAll
    static void createHttpTaskManager() {

    }

    @BeforeEach
    void initHttpTaskManager() {
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        manager = new HttpTaskManager("http://localhost:" + KVServer.PORT);
        initManager();
    }


    @AfterEach
    void afterEach() {
        kvServer.stop();
    }

    @Test
    void init() {
    }

    @Test
    void loadTest() {
        manager.createTask(new Task("nameT", "descr.T"));
        manager.createTask(new Task("nameT2", "descr.T2"));
        HttpTaskManager newHttpTaskManager = new HttpTaskManager("http://localhost:" + KVServer.PORT);
        newHttpTaskManager.load();
        List<Task> allTasks = newHttpTaskManager.getAllTasks();
        System.out.println("HttpTaskManagerTest.loadTest() allTasks:\n" + allTasks);
    }

    @Test
    void saveTest() {
        manager.save();
    }

    @Test
    void createTasksTest() {
        manager.createTask(new Task("nameT", "descr.T"));
        manager.createTask(new Task("nameT2", "descr.T2"));
        Epic epic = new Epic("nameE", "descr.E");
        manager.createEpic(epic);
    }

}