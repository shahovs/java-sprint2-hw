package http;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Постман: https://www.getpostman.com/collections/a83b61d9e1c81c10575c
 * {"info":
 * {"_postman_id":"21a3bc32-f829-4054-a6bf-5fc85d49933e",
 * "name":"Yjava KVServer",
 * "schema":"https://schema.getpostman.com/json/collection/v2.0.0/collection.json"},
 * "item":[
 * <p>
 * {"name":"Register","id":"fe2c2ee3-3727-4132-b03f-7485783626eb",
 * "request":{"method":"GET","header":[],"url":"http://localhost:8078/register"},
 * "response":[]},
 * <p>
 * {"name":"save","id":"87b178af-cf99-4ec5-9c6c-b1804a4318da",
 * "request":{
 * "method":"POST",
 * "header":[],
 * "body":{"mode":"raw","raw":"My value"},
 * "url":{"raw":"http://localhost:8078/save/mykey?API_KEY=DEBUG",
 * "protocol":"http",
 * "host":["localhost"],
 * "port":"8078",
 * "path":["save","mykey"],
 * "query":[{"key":"API_KEY","value":"DEBUG"}]}},
 * "response":[]},
 * <p>
 * {"name":"load","id":"cb85d0c1-f5a6-4e35-8ca1-e559acc549da",
 * "request":{
 * "method":"GET",
 * "header":[],
 * "url":{"raw":"http://localhost:8078/load/mykey?API_KEY=DEBUG",
 * "protocol":"http",
 * "host":["localhost"],
 * "port":"8078",
 * "path":["load","mykey"],
 * "query":[{"key":"API_KEY","value":"DEBUG"}]}},
 * "response":[]}]}
 */

public class KVServer {
    public static final int PORT = 8078;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private void load(HttpExchange h) {
        try {
            System.out.println("\n/load");
            if (!hasAuth(h)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                if (!data.containsKey(key)) {
                    System.out.println("Не найдена запись по ключу " + key);
                    h.sendResponseHeaders(404, 0);
                    return;
                }
                String value = data.get(key);
                System.out.println("value:\n" + value);
//                Gson gson = new Gson();
//                String json = gson.toJson(value);
//                System.out.println("json: " + json);
                System.out.println("Значение для ключа " + key + " отправляется!");
                h.sendResponseHeaders(200, 0);
                try (OutputStream os = h.getResponseBody()) {
                    os.write(value.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            h.close();
        }
    }

    private void save(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/save");
            if (!hasAuth(h)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void register(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/register");
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, apiToken);
            } else {
                System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json"); // TODO У меня этого нигде нет (добавить)
        h.sendResponseHeaders(200, resp.length); // TODO и этого тоже у меня нет (length)
        h.getResponseBody().write(resp);
    }
}

