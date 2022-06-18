package http;
//TODO Этот класс можно удалить.
// Он пригодился для запуска серверов, чтобы использовать Insomnia

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class HttpTaskClient {
    static HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) {
        runHttpTaskServer();
        runKVServer();
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
            System.out.println("After response kvServer");
            if (response.statusCode() == 200) {
                System.out.println("Код ответа 200");
            } else {
                System.out.println("Код ответа: ");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void runHttpTaskServer() {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.PORT + HttpTaskServer.pathSubtask);
        HttpRequest request = HttpRequest.newBuilder()
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
                System.out.println("Код ответа: ");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
