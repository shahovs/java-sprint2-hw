package http;

import model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.LocalDateTime;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;


class HttpTaskServerTest {
    static HttpTaskServer httpTaskServer;
    HttpClient client;

    @BeforeAll
    static void createAndRunHttpTaskServer() {
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.runServer();
        //new HttpTaskServer().runServer();
    }

    @BeforeEach
    void createHttpClient() {
        client = HttpClient.newHttpClient();
    }

//    @AfterEach
//    void afterEach() {
//    }
//
//    @AfterAll
//    static void stopTaskServer() {
//        httpTaskServer.stopServer();
//    }

    @Test
    void getAllTasks() {
        createAndSendGetRequestAndCheckResponse(HttpTaskServer.uriHeadOnly, "getAllTasks");
    }

    @Test
    void getPrioritizedTasks() {
        createAndSendGetRequestAndCheckResponse(HttpTaskServer.uriPrioritized, "getPrioritizedTasks");
    }

    @Test
    void getHistory() {
        createAndSendGetRequestAndCheckResponse(HttpTaskServer.uriHistory, "getHistory");
    }

    private void createAndSendGetRequestAndCheckResponse(String path, String methodName) {
        URI uri = URI.create(path);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
            System.out.println("Тело ответа метода " + methodName + ":\n" + response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getTaskById() {
        URI uri = URI.create(HttpTaskServer.uriTask + "?id=1");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri)
                .header("Accept", "application/json").build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
            System.out.println("Тело ответа (getTaskById):\n" + response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    } // TODO Сделать тесты с неправильными id (0, 1000, -1, 1.2, "c", "char")

    @Test
    void postTaskWithDataTime() {
        URI uri = URI.create(HttpTaskServer.uriTask);
        Gson gson = new Gson();
        String json = gson.toJson(new Task("nameTask", "description", 0, Task.Status.NEW,
                LocalDateTime.of(2022, 1, 1, 1, 1, 1), 1));
        System.out.println("json(new Task):\n" + json);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(uri).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
            System.out.println("Тело ответа(postTask):\n" + response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void postSubtaskWithoutDataTime() {
        URI uri = URI.create(HttpTaskServer.uriSubtask);
        Gson gson = new Gson();
        String json = gson.toJson(new Task("nameSubtask", "description"));
        System.out.println("json(new Subtask):\n" + json);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(uri).build(); // .header(...)
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
            System.out.println("Тело ответа(postSubtask):\n" + response.body());
        } catch (IOException |
                InterruptedException e) {
            e.printStackTrace();
        }
    }


} // end of class