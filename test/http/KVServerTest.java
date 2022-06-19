package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


class KVServerTest {
    final static String localHostAndPort = "http://localhost:" + KVServer.PORT;
    final static String uriRegister = localHostAndPort + "/register";
    final static String uriSave = localHostAndPort + "/save";
    final static String uriLoad = localHostAndPort + "/load";


    static KVServer server;
    static Gson gson;
    static HttpClient client;
    static String token;
    static final int key = 1;

    @BeforeAll
    static void beforeAll() {
        System.out.println("*************************************************************************");
        System.out.println("Запускаем тесты класса KVServerTest");
        try {
            server = new KVServer();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        client = HttpClient.newHttpClient();
        token = registerTest(); // следующие тесты нужно запускать по порядку, поэтому они здесь (TODO исправить)
        saveTest();
        loadTest();
        reSaveTest();
        secondLoadTest();
    }


    @AfterAll
    static void stopKVServer() {
        server.stop();
    }

    @Test
    void test(){}

    static String registerTest() {
        String result = "DEBUG"; // значение по умолчанию
        URI uri = URI.create(uriRegister);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
            System.out.println("Тело ответа метода registerTest(): " + response.body());
            result = response.body();
            System.out.println("token: " + result);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    static void saveTest() {
        URI uri = URI.create(uriSave + "/" + key + "?API_TOKEN=" + token);
        String json = gson.toJson(new Task("taskName", "descr."));
        System.out.println("\nsaveTest() json:\n" + json);
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

    static void loadTest() {
        URI uri = URI.create(uriLoad + "/" + key + "?API_TOKEN=" + token);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
            System.out.println("Тело ответа метода loadTest():\n" + response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void reSaveTest() {
        URI uri = URI.create(uriSave + "/" + key + "?API_TOKEN=" + token);
        String json = gson.toJson(new Task("newTaskName", "reSave"));
        System.out.println("\nreSaveTest() json:\n" + json);
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

    static void secondLoadTest() {
        URI uri = URI.create(uriLoad + "/" + key + "?API_TOKEN=" + token);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Код ответа не равен 200");
            System.out.println("Тело ответа метода secondLoadTest():\n" + response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}