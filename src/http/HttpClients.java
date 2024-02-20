package http;
// Этот класс используется для запуска нужных серверов без последующей их остановки.
// Полезен, чтобы тестировать программу через Insomnia или браузер
// В основном коде программы он не используется.

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class HttpClients {
    static HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    public static void main(String[] args) {
        runKVServer();
        runHttpTaskServer();
    }

    private static void runKVServer() {
        try {
            KVServer kvServer = new KVServer();
            kvServer.start();
            URI uri = URI.create("http://localhost:" + KVServer.PORT + "/register");
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .build();
            System.out.println("Before response kvServer");
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            System.out.println("After response kvServer\n");
            if (response.statusCode() == 200) {
                System.out.println("Код ответа 200");
            } else {
                System.out.println("Код ответа: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void runHttpTaskServer() {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + HttpTaskServer.TASKS_TASK);

        String json = gson.toJson(new Task("TaskName", "(description)", 0, Task.Status.NEW,
                LocalDateTime.of(2022, 10, 1, 12, 0, 0), 10));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(uri).build();

        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                System.out.println("Код не равен 200, а равен " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        /*HttpRequest request1 = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        try {
            System.out.println("Before response HttpTaskServer");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("After response HttpTaskServer");
            if (response.statusCode() == 200) {
                System.out.println("Код ответа 200");
            } else {
                System.out.println("Код ответа: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }*/
    }
}
