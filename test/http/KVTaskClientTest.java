package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

class KVTaskClientTest {
    static KVTaskClient kvTaskClient;
    static Gson gson;
    static KVServer kvServer;

    @BeforeAll
    static void createKVClient() {
        System.out.println("*************************************************************************");
        System.out.println("Запускаем тесты класса KVTaskClientTest");
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        kvTaskClient = new KVTaskClient("http://localhost:" + KVServer.PORT);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @AfterAll
    static void stopKVServer() {
        kvServer.stop();
    }

    @Test
    void putTest() {
        String json = gson.toJson(new Task("taskName", "putTest()"));
        System.out.println("\nputTest() json:\n" + json);
        kvTaskClient.put("putTest", json);
    }

    @Test
    void loadTest() {
        String key = "loadTest";
        String json = gson.toJson(new Task("taskName", "loadTest()"));
        kvTaskClient.put(key, json);
        String result = kvTaskClient.load(key);
        System.out.println("\nloadTest() json(result):\n" + result);
    }

    @Test
    void loadTestTwice() {
        String key = "loadTestTwice";
        String json = gson.toJson(new Task("taskName", "first"));
        kvTaskClient.put(key, json);
        json = gson.toJson(new Task("newTaskName", "second"));
        kvTaskClient.put(key, json);
        String result = kvTaskClient.load(key);
        System.out.println("\nloadTestTwice() json(result):\n" + result);
    }
}