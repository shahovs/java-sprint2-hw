package http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    static HttpClient client;
    URI uri;
    String token;

    public KVTaskClient(String url) {
        uri = URI.create(url);
        try {
            client = HttpClient.newHttpClient();
            URI uriRegister = URI.create(uri + "/register");
            HttpRequest request = HttpRequest.newBuilder().GET().uri(uriRegister).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Код не равен 200, а равен " + response.statusCode());
                return;
            }
            System.out.println("Тело ответа конструктора класса KVTaskClient: " + response.body());
            token = response.body();
            System.out.println("token: " + token);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // должен сохранять состояние менеджера задач через запрос POST /save/<ключ>?API_TOKEN=
    public void put(String key, String json) {

        URI uriSave = URI.create(uri + "/save" + "/" + key + "?API_TOKEN=" + token);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(uriSave).build();
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                System.out.println("Код не равен 200, а равен " + response.statusCode());
            }            //System.out.println("response.statusCode()=" + response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_TOKEN=
    public String load(String key) {
        System.out.println("\nKVTaskClient /load");
        URI uriLoad = URI.create(uri + "/load" + "/" + key + "?API_TOKEN=" + token);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uriLoad).build();
        String result = "";
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Код не равен 200, а равен " + response.statusCode());
                return result;
            }
            result = response.body();
            System.out.println("Тело ответа метода load():\n" + result);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
