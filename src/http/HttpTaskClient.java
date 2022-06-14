package http;
//TODO Этот класс нужно удалить. Он должен быть в тестах.

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class HttpTaskClient {
    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.runServer();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(HttpTaskServer.uriSubtask);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        try {
            System.out.println("Before response");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("After response");
            if (response.statusCode() == 200) {
                System.out.println("Код ответа 200");
            } else {
                System.out.println("Код ответа: ");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        //httpTaskServer.stopServer();
    }
}
