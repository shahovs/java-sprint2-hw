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
    final static URI LOCAL_HOST_AND_PORT = URI.create("http://localhost:" + HttpTaskServer.PORT); // "http://localhost:8080"
    final static URI URI_HEAD_ONLY = URI.create(LOCAL_HOST_AND_PORT + HttpTaskServer.PATH_BEGIN_ONLY);
    final static URI URI_TASK = URI.create(LOCAL_HOST_AND_PORT + HttpTaskServer.TASKS_TASK);
    final static URI URI_SUBTASK = URI.create(LOCAL_HOST_AND_PORT + HttpTaskServer.TASKS_SUBTASK); // "...:8080/tasks/subtask
    final static URI URI_EPIC = URI.create(LOCAL_HOST_AND_PORT + HttpTaskServer.TASKS_EPIC); // "...:8080/tasks/epic
    final static URI URI_HISTORY = URI.create(LOCAL_HOST_AND_PORT + HttpTaskServer.TASKS_HISTORY); // "...:8080/tasks/history
    final static URI URI_PRIORITIZED = URI.create(LOCAL_HOST_AND_PORT + HttpTaskServer.TASKS_PRIORITIZED); // ".../tasks/prioritized

    static KVServer kvServer;
    static HttpClient client; // Это мы. Пользователь, который создает задачу и делает запросы по http
    static Gson gson; // Задачи мы передаем по http в формате json
    static HttpTaskServer httpTaskServer; // Также мы запускаем сервер, на который передаем свои запросы
//     Также мы запускаем KVServer, который будет получать, хранить и возвращать состояние менеджера задач

    @BeforeAll
    static void beforeAll() {
        System.out.println("*************************************************************************");
        System.out.println("Запускаем тесты класса HttpTaskServerTest");
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    void beforeEach() {
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpTaskServer = new HttpTaskServer();
        // запуск start() осуществляется автоматически вместе с созданием объекта
        // (метод start() вызывается конструктором HttpTaskServer)
    }

    @AfterEach
    void afterEach() {
        httpTaskServer.stop();
        kvServer.stop();
    }

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

    private void createAndSendGetRequestAndCheckResponse(URI uri, String methodName) {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
            System.out.println("Тело ответа метода " + methodName + ":\n" + response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createAndSendWrongMethodDelete(URI uri, String methodName) {
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(405, response.statusCode(), "Код ответа не равен 404");
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
            assertEquals(400, response.statusCode(), "Код ответа не равен 404");
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
            assertEquals(400, response.statusCode(), "Код ответа не равен 404");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getTaskByIdWithWrongIdDouble() {
        URI uri = URI.create(URI_TASK + "/?id=1.1");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri)
                .header("Accept", "application/json").build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode(), "Код ответа не равен 404");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void postTaskWithDataTime() {
        String json = gson.toJson(new Task("nameTask", "withDataTime", 0, Task.Status.DONE,
                LocalDateTime.of(2022, 12, 31, 23, 59, 0), 50));
        System.out.println("json(new Task):\n" + json);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(URI_TASK).build();
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void postTaskWithoutDataTime() {
        String json = gson.toJson(new Task("nameTask", "withoutDataTime"));
        System.out.println("json(new Task):\n" + json);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(URI_TASK).build(); // .header(...)
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
        } catch (IOException |
                InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void postEpic() {
        String json = gson.toJson(new Epic("nameEpic", "withDataTime", 0));
        System.out.println("json(new Epic):\n" + json);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(URI_EPIC).build();
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
            //System.out.println("response.statusCode()=" + response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    Epic getEpicForTests() {
        // id эпика должен быть равен 1, так как subtask должна быть создана с конкретным эпиком
        // иначе тесты с subtask не пройдут
        Epic epic = new Epic("nameEpic", "withDataTime", 1);
        String json = gson.toJson(epic);
        System.out.println("json(new Epic):\n" + json);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(URI_EPIC).build();
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
            //System.out.println("response.statusCode()=" + response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return epic;
    }

    @Test
    void postSubtaskWithoutDataTime() {
        Epic epic = getEpicForTests();
        String json = gson.toJson(new Subtask("nameSubtask", "description", 0, Task.Status.NEW, epic));
        System.out.println("json(new Subtask):\n" + json);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(URI_SUBTASK).build(); // .header(...)
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
        } catch (IOException |
                InterruptedException e) {
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

} // end of class