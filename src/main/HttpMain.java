package main;
// Проверяем отсюда работу всей цепочки передачи информации по http
// (чтобы не запускать сразу все тесты всех классов)

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HttpTaskServer;
import http.KVServer;
import http.LocalDateTimeAdapter;
import model.Task;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.LocalDateTime;

public class HttpMain {

    static KVServer kvServer;
    static HttpTaskServer httpTaskServer;
    static Gson gson;
    static HttpClient client;

    static {
        System.out.println("*************************************************************************");
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
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
        client = HttpClient.newHttpClient();
    }

    public static void main(String[] args) {
        System.out.println("*************************************************************************");
        System.out.println("Запускаем тесты класса HttpMain");

        String json;
        Task task01 = new Task("(T1)", "(d)");
        json = gson.toJson(task01);
        System.out.println("HttpMain.main() toJson(task01):\n" + json);






        stopServers();
    }

    private static void stopServers() {
        // Если нужно сделать запросы из браузера или Insomnia, то можно запустить серверы из класса HttpClients
        // В этом случае серверы остановлены не будут.
        System.out.println("*************************************************************************");
        System.out.println("Останавливаем серверы");
        httpTaskServer.stop();
        kvServer.stop();
    }
}
