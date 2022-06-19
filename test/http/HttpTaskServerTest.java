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
    final static String LOCAL_HOST_AND_PORT = "http://localhost:" + HttpTaskServer.PORT; // "http://localhost:8080"
    final static String URI_HEAD_ONLY = LOCAL_HOST_AND_PORT + HttpTaskServer.PATH_BEGIN_ONLY; // "http:...:8080/tasks
    final static String URI_TASK = LOCAL_HOST_AND_PORT + HttpTaskServer.TASKS_TASK; // "http:...:8080/task
    final static String URI_SUBTASK = LOCAL_HOST_AND_PORT + HttpTaskServer.TASKS_SUBTASK; // "...:8080/tasks/subtask
    final static String URI_EPIC = LOCAL_HOST_AND_PORT + HttpTaskServer.TASKS_EPIC; // "...:8080/tasks/epic
    final static String URI_HISTORY = LOCAL_HOST_AND_PORT + HttpTaskServer.TASKS_HISTORY; // "...:8080/tasks/history
    final static String URI_PRIORITIZED = LOCAL_HOST_AND_PORT + HttpTaskServer.TASKS_PRIORITIZED; // ".../tasks/prioritized


    static HttpClient client; // Это мы. Пользователь, который создает задачу и делает запросы по http
    static Gson gson; // Задачи мы передаем по http в формате json
    static HttpTaskServer httpTaskServer; // Также мы запускаем сервер, на который передаем свои запросы
//     Также мы запускаем KVServer, который будет получать, хранить и возвращать состояние менеджера задач

    @BeforeAll
    static void beforeAll() {
        System.out.println("*************************************************************************");
        System.out.println("Запускаем тесты класса HttpTaskServerTest");
        httpTaskServer = new HttpTaskServer();
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
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
        createAndSendGetRequestAndCheckResponse(URI_HEAD_ONLY, "getAllTasks");
    }

    @Test
    void getPrioritizedTasks() {
        createAndSendGetRequestAndCheckResponse(URI_PRIORITIZED, "getPrioritizedTasks");
    }

    @Test
    void getHistory() {
        createAndSendGetRequestAndCheckResponse(URI_HISTORY, "getHistory");
    }

    @Test
    void getAllTasksWithError() {
        createAndSendWrongMethodDelete(URI_HEAD_ONLY, "getAllTasks");
    }

    @Test
    void getPrioritizedTasksWithError() {
        createAndSendWrongMethodDelete(URI_PRIORITIZED, "getPrioritizedTasks");
    }

    @Test
    void getHistoryWithError() {
        createAndSendWrongMethodDelete(URI_HISTORY, "getHistory");
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
        URI uri = URI.create(URI_TASK + "/?id=1");
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
        URI uri = URI.create(URI_TASK + "/?id=-1");
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
        URI uri = URI.create(URI_TASK + "/?id=incorrectId");
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
        URI uri = URI.create(URI_TASK + "/?id=1.1");
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
        URI uri = URI.create(URI_TASK);
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
        URI uri = URI.create(URI_TASK + "/?id=1");
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
        URI uri = URI.create(URI_TASK);
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
        URI uri = URI.create(URI_SUBTASK);
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