package main;
// Проверяем отсюда работу всей цепочки передачи информации по http
// (чтобы не запускать сразу все тесты всех классов)

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.EpicAdapter;
import http.HttpTaskServer;
import http.KVServer;
import http.LocalDateTimeAdapter;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class HttpMain {

    final static URI LOCAL_HOST_AND_PORT = URI.create("http://localhost:" + HttpTaskServer.PORT); // "http://localhost:8080"
    final static URI URI_HEAD_ONLY = URI.create(LOCAL_HOST_AND_PORT + HttpTaskServer.PATH_BEGIN_ONLY);
    final static URI URI_TASK = URI.create(LOCAL_HOST_AND_PORT + HttpTaskServer.TASKS_TASK);
    final static URI URI_SUBTASK = URI.create(LOCAL_HOST_AND_PORT + HttpTaskServer.TASKS_SUBTASK); // "...:8080/tasks/subtask
    final static URI URI_EPIC = URI.create(LOCAL_HOST_AND_PORT + HttpTaskServer.TASKS_EPIC); // "...:8080/tasks/epic
    final static URI URI_HISTORY = URI.create(LOCAL_HOST_AND_PORT + HttpTaskServer.TASKS_HISTORY); // "...:8080/tasks/history
    final static URI URI_PRIORITIZED = URI.create(LOCAL_HOST_AND_PORT + HttpTaskServer.TASKS_PRIORITIZED); // ".../tasks/prioritized

    static KVServer kvServer;
    static HttpTaskServer httpTaskServer;
    static Gson gson;
    static HttpClient client;

    static {
        System.out.println("\n*************************************************************************");
        System.out.println("Запускаем оба сервера");
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // HttpTaskServer нужно запускать строго после KVServer, иначе HttpTaskServer не с чем будет соединяться
        httpTaskServer = new HttpTaskServer(); // запуск start() происходит в конструкторе вместе с созданием
    }

    static {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Epic.class, new EpicAdapter())
                .setPrettyPrinting()
                .create();
        client = HttpClient.newHttpClient();
    }

    public static void main(String[] args) {
        System.out.println("\n*************************************************************************");
        System.out.println("Запускаем тесты класса HttpMain");

        Task taskT1 = new Task("(T1)", "(noTime)");
        Task taskT2 = new Task("(T2)", "(hasTime)", 0, Task.Status.NEW,
                LocalDateTime.of(2022, 12, 31, 23, 59, 0), 50);
        Epic epicE1 = new Epic("(E1)", "(d)", 3);
        Subtask subtaskS1 = new Subtask("(S1)", "(noTime)", epicE1);

        String jsonT1 = gson.toJson(taskT1);
        System.out.println("HttpMain.main() toJson(taskT1):\n" + jsonT1);
        String jsonT2 = gson.toJson(taskT2);
        System.out.println("HttpMain.main() toJson(taskT2):\n" + jsonT2);
        String jsonE1 = gson.toJson(epicE1);
        System.out.println("HttpMain.main() toJson(epicE1):\n" + jsonE1);
        String jsonS1 = gson.toJson(subtaskS1);
        System.out.println("HttpMain.main() toJson(subtaskS1):\n" + jsonS1);

        final HttpRequest.BodyPublisher bodyT1 = HttpRequest.BodyPublishers.ofString(jsonT1);
        HttpRequest requestPostTask1 = HttpRequest.newBuilder().POST(bodyT1).uri(URI_TASK).build();

        final HttpRequest.BodyPublisher bodyT2 = HttpRequest.BodyPublishers.ofString(jsonT2);
        HttpRequest requestPostTask2 = HttpRequest.newBuilder().POST(bodyT2).uri(URI_TASK).build();

        final HttpRequest.BodyPublisher bodyE1 = HttpRequest.BodyPublishers.ofString(jsonE1);
        HttpRequest requestPostEpic1 = HttpRequest.newBuilder().POST(bodyE1).uri(URI_EPIC).build();

        final HttpRequest.BodyPublisher bodyS1 = HttpRequest.BodyPublishers.ofString(jsonS1);
        HttpRequest requestPostSubtask1 = HttpRequest.newBuilder().POST(bodyS1).uri(URI_SUBTASK).build();

        HttpRequest requestGetAllTasks = HttpRequest.newBuilder().GET().uri(URI_HEAD_ONLY).build();

        try {
            HttpResponse<Void> responsePost;
            responsePost = client.send(requestPostTask1, HttpResponse.BodyHandlers.discarding());
            if (responsePost.statusCode() != 200) {
                System.out.println("HttpMain.main() statusCode != 200 StatusCode = " + responsePost.statusCode());
            }
            responsePost = client.send(requestPostTask2, HttpResponse.BodyHandlers.discarding());
            if (responsePost.statusCode() != 200) {
                System.out.println("HttpMain.main() statusCode != 200 StatusCode = " + responsePost.statusCode());
            }
            responsePost = client.send(requestPostEpic1, HttpResponse.BodyHandlers.discarding());
            if (responsePost.statusCode() != 200) {
                System.out.println("HttpMain.main() requestPostEpic1 statusCode != 200 StatusCode = " + responsePost.statusCode());
            }
            responsePost = client.send(requestPostSubtask1, HttpResponse.BodyHandlers.discarding());
            if (responsePost.statusCode() != 200) {
                System.out.println("HttpMain.main() requestPostSubtask1 statusCode != 200 StatusCode = " + responsePost.statusCode());
            }
            HttpResponse<String> responseGetAll = client.send(requestGetAllTasks, HttpResponse.BodyHandlers.ofString());
            if (responseGetAll.statusCode() != 200) {
                System.out.println("HttpMain.main() statusCode != 200 StatusCode = " + responseGetAll.statusCode());
            }
            System.out.println("Тело ответа метода responseGetAll" + ":\n" + responseGetAll.body());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }




        stopServers();
    }

    private static void stopServers() {
        // Если нужно сделать запросы из браузера или Insomnia, то можно запустить серверы из класса HttpClients
        // В этом случае серверы остановлены не будут.
        System.out.println("\n*************************************************************************");
        System.out.println("Останавливаем серверы");
        httpTaskServer.stop();
        kvServer.stop();
    }
}
