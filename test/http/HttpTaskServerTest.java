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
import com.google.gson.GsonBuilder;

class HttpTaskServerTest {
    final static String localHostAndPort = "http://localhost:" + HttpTaskServer.PORT;
    final static String uriHeadOnly = localHostAndPort + HttpTaskServer.pathBeginOnly;
    final static String uriTask = localHostAndPort + HttpTaskServer.pathTask;
    final static String uriSubtask = localHostAndPort + HttpTaskServer.pathSubtask;
    final static String uriEpic = localHostAndPort + HttpTaskServer.pathEpic;
    final static String uriHistory = localHostAndPort + HttpTaskServer.pathHistory;
    final static String uriPrioritized = localHostAndPort + HttpTaskServer.pathPrioritized;

    static HttpTaskServer httpTaskServer;
    static Gson gson;
    static HttpClient client;

    @BeforeAll
    static void beforeAll() {
        httpTaskServer = new HttpTaskServer();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        client = HttpClient.newHttpClient();

        //httpTaskServer.runServer();
        //new HttpTaskServer().runServer();
    }

//    @BeforeEach
//    void createHttpClient() {
//        client = HttpClient.newHttpClient();
//    }

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
        createAndSendGetRequestAndCheckResponse(uriHeadOnly, "getAllTasks");
    }

    @Test
    void getPrioritizedTasks() {
        createAndSendGetRequestAndCheckResponse(uriPrioritized, "getPrioritizedTasks");
    }

    @Test
    void getHistory() {
        createAndSendGetRequestAndCheckResponse(uriHistory, "getHistory");
    }

    @Test
    void getAllTasksWithError() {
        createAndSendWrongMethodDelete(uriHeadOnly, "getAllTasks");
    }

    @Test
    void getPrioritizedTasksWithError() {
        createAndSendWrongMethodDelete(uriPrioritized, "getPrioritizedTasks");
    }

    @Test
    void getHistoryWithError() {
        createAndSendWrongMethodDelete(uriHistory, "getHistory");
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

    private void createAndSendWrongMethodDelete(String path, String methodName) {
        URI uri = URI.create(path);
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode(), "Код ответа не равен 404");
            assertEquals("", response.body(), "Тело ответа на ошибочный запрос не равно пустой строке");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getTaskById() {
        URI uri = URI.create(uriTask + "/?id=1");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri)
                .header("Accept", "application/json").build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
            System.out.println("Тело ответа (getTaskById):\n" + response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    void getTaskByIdWithWrongId() {
        URI uri = URI.create(uriTask + "/?id=-1");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri)
                .header("Accept", "application/json").build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode(), "Код ответа не равен 404");
            //System.out.println("Тело ответа (getTaskById):\n" + response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getTaskByIdWithIncorrectId() {
        URI uri = URI.create(uriTask + "/?id=incorrectId");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri)
                .header("Accept", "application/json").build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode(), "Код ответа не равен 404");
            //System.out.println("Тело ответа (getTaskById):\n" + response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    } // TODO Сделать тесты с неправильными id (0, 1000)

    @Test
    void getTaskByIdWithWrongIdDouble() {
        URI uri = URI.create(uriTask + "/?id=1.1");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri)
                .header("Accept", "application/json").build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode(), "Код ответа не равен 404");
            //System.out.println("Тело ответа (getTaskById):\n" + response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void postTaskWithDataTime() {
        URI uri = URI.create(uriTask);
        String json = gson.toJson(new Task("nameTask", "withDataTime", 0, Task.Status.DONE,
                LocalDateTime.of(2022, 12, 31, 23, 59, 0), 50));
        System.out.println("json(new Task):\n" + json);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(uri).build();
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
            //System.out.println("response.statusCode()=" + response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteTaskById() {
        URI uri = URI.create(uriTask + "/?id=1");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri)
                .header("Accept", "application/json").build();
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void postTaskWithoutDataTime() {
        URI uri = URI.create(uriTask);
        String json = gson.toJson(new Task("nameTask", "withoutDataTime"));
        System.out.println("json(new Task):\n" + json);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(uri).build(); // .header(...)
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
        } catch (IOException |
                InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void postSubtaskWithoutDataTime() {
        URI uri = URI.create(uriSubtask);
        String json = gson.toJson(new Task("nameSubtask", "description"));
        System.out.println("json(new Subtask):\n" + json);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(uri).build(); // .header(...)
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
        } catch (IOException |
                InterruptedException e) {
            e.printStackTrace();
        }
    }


} // end of class