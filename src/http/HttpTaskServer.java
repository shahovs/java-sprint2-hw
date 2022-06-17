package http;

// ИТОГО ОСТАЛОСЬ ДОДЕЛАТЬ В ЭТОМ КЛАССЕ:
// - обработка эпиков и сабтасков (по аналогии с тасками, но с особенностями - содержащимися в них элементами)
// - заменить файл менеджер на сервер менеджер
// - все протестировать

import java.net.InetSocketAddress;

import com.google.gson.*;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import manager.Managers;
import manager.TaskManager;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.net.URI;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.TreeSet;


public class HttpTaskServer {
    public static final int PORT = 8080;
    public final static String pathBeginOnly = "/tasks"; // Переделать все константы в верхнем регистре
    public final static String pathTask = "/tasks/task";
    public final static String pathSubtask = "/tasks/subtask";
    public final static String pathEpic = "/tasks/epic";
    public final static String pathHistory = "/tasks/history";
    public final static String pathPrioritized = "/tasks/prioritized";
    public final static String idAttribute = "id=";

    private final Gson gson;
    private final TaskManager manager;

    public HttpTaskServer() {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext(pathBeginOnly, new tasksHandler()); // сделал такой вариант для примера
            httpServer.createContext(pathPrioritized, this::prioritizedHandle);
            httpServer.createContext(pathHistory, this::historyHandle);
            httpServer.createContext(pathTask, this::taskHandle);
            httpServer.createContext(pathSubtask, this::subtaskHandle);
            httpServer.createContext(pathEpic, this::epicHandle);
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Сделал вариант с внутренним классом для примера
    // path: "/tasks/"
    class tasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if (!"GET".equals(httpExchange.getRequestMethod())) {
                httpExchange.sendResponseHeaders(404, 0);
                httpExchange.close();
                return;
            }
            try {
                System.out.println("RequestURI: " + httpExchange.getRequestURI());

                httpExchange.sendResponseHeaders(200, 0);
                List<Task> allTasks = manager.getAllTasks();
                allTasks.addAll(manager.getAllSubtasks());
                allTasks.addAll(manager.getAllEpics());
                String json = gson.toJson(allTasks);
                sendBodyAndClose(httpExchange, json);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // path: "/tasks/prioritized"
    private void prioritizedHandle(HttpExchange httpExchange) throws IOException {
        if (!"GET".equals(httpExchange.getRequestMethod())) {
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
            return;
        }
        try {
            System.out.println("RequestURI: " + httpExchange.getRequestURI());

            httpExchange.sendResponseHeaders(200, 0);
            TreeSet<Task> prioritizedTasks = manager.getPrioritizedTasks();
            String json = gson.toJson(prioritizedTasks);
            sendBodyAndClose(httpExchange, json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // path: "/tasks/history"
    private void historyHandle(HttpExchange httpExchange) throws IOException {
        if (!"GET".equals(httpExchange.getRequestMethod())) {
            httpExchange.sendResponseHeaders(404, 0);
            httpExchange.close();
            return;
        }
        try {
            System.out.println("RequestURI: " + httpExchange.getRequestURI());

            httpExchange.sendResponseHeaders(200, 0);
            List<Task> history = manager.getHistory();
            String json = gson.toJson(history);
            sendBodyAndClose(httpExchange, json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // path: "/tasks/task/" or "/tasks/task/?id=.."
    private void taskHandle(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("RequestURI: " + httpExchange.getRequestURI());

            String method = httpExchange.getRequestMethod();
            boolean hasIdQuery = checkIdQuery(httpExchange);
            //URI uri = httpExchange.getRequestURI();
            //System.out.println("queryComponent: " + queryComponent); // id=1 (если нет знака вопроса ?, то возвращает null)
            //String uriString = uri.toString();
            //System.out.println("uriString: " + uriString); // /tasks/task/?id=1
            //String queryComponent = uri.getQuery(); // "id=1"
            //System.out.println("queryComponent " + queryComponent);
            //String pathWithIdComponent = pathTask + idAttribute;
            //System.out.println("pathWithIdComponent: " + pathWithIdComponent); // /tasks/task/?id=

            if (hasIdQuery) {
                // path: "/tasks/task/?id=.."
                int id = getId(httpExchange);
                if (id < 0) {
                    return;
                }
                switch (method) {
                    case "GET":
                        httpExchange.sendResponseHeaders(200, 0);
                        Task task = manager.getTask(id);
                        String json = gson.toJson(task);
                        System.out.println("getTask(id) json\n" + json);
                        sendBodyAndClose(httpExchange, json);
                        break;
                    case "DELETE":
                        manager.removeTask(id);
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    default:
                        httpExchange.sendResponseHeaders(404, 0);
                }
            } else {
                // if uri has NO id
                // path: "/tasks/task"
                switch (method) {
                    case "GET":
                        httpExchange.sendResponseHeaders(200, 0);
                        List<Task> allTasks = manager.getAllTasks();
                        String json = gson.toJson(allTasks);
                        System.out.println("getAllTasks() json\n" + json); // удалить после окончания разработки
                        sendBodyAndClose(httpExchange, json);
                        break;
                    case "DELETE":
                        manager.removeAllTasks();
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    case "POST":
                        Task newTask = getTask(httpExchange);
                        if (newTask == null) {
                            return;
                        }
                        manager.createTask(newTask);
                        httpExchange.sendResponseHeaders(200, 0); // подумать о кодах ответа
                        break;
                    case "PUT":
                        Task updatedTask = getTask(httpExchange);
                        if (updatedTask == null) {
                            return;
                        }
                        manager.updateTask(updatedTask);
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    default:
                        httpExchange.sendResponseHeaders(404, 0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private boolean checkIdQuery(HttpExchange httpExchange) {
        URI uri = httpExchange.getRequestURI();
        String queryComponent = uri.getQuery(); // "id=1" or null
        if (queryComponent != null && queryComponent.startsWith(idAttribute)) {
            return true;
        }
        return false;
    }

    private int getId(HttpExchange httpExchange) throws IOException {
        URI uri = httpExchange.getRequestURI();
        String queryComponent = uri.getQuery(); // "id=1" or null
        int id = -1;
        if (queryComponent != null && queryComponent.startsWith(idAttribute)) {
            String idString = queryComponent.substring(idAttribute.length());
            try {
                id = Integer.parseInt(idString); //System.out.println("id=" + id);
            } catch (NumberFormatException e) {
                send404AndClose(httpExchange);
                e.printStackTrace();
                return -1;
            }
        }
        return id;
    }

    private Task getTask(HttpExchange httpExchange) {
        try (InputStream inputStream = httpExchange.getRequestBody();) {
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("POST/PUT body:\n" + body);

            JsonElement jsonElement = JsonParser.parseString(body);
            if (!jsonElement.isJsonObject()) {
                httpExchange.sendResponseHeaders(404, 0);
                httpExchange.close();
                return null;
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            String statusString = jsonObject.get("status").getAsString();
            Task.Status status = Task.Status.NEW;
            try {
                status = Task.Status.valueOf(statusString);
            } catch (IllegalArgumentException | NullPointerException e) {
                e.printStackTrace();
            }

            //String localDateTimeString = jsonObject.get("startTime").getAsString();
            //System.out.println("localDateTimeString: " + localDateTimeString);
            //LocalDateTime localDateTime = gson.fromJson(localDateTimeString, LocalDateTime.class);
            LocalDateTime startTime = gson.fromJson(jsonObject.get("startTime"), LocalDateTime.class);
            //System.out.println("localDateTime: " + startTime);

            int duration = jsonObject.get("duration").getAsInt();
            return new Task(name, description, 0, status, startTime, duration);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // path: "/tasks/subtask/"
    private void subtaskHandle(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.close();
    }

    // path: "/tasks/epic/"
    private void epicHandle(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.close();
    }

    private void send404AndClose(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(404, 0);
        httpExchange.close();
    }

    private void sendBodyAndClose(HttpExchange httpExchange, String responseBody) throws IOException {
        try (OutputStream os = httpExchange.getResponseBody();) {
            os.write(responseBody.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }


//     Метод не работает. Как остановить сервер?
//     (тесты умеют останавливать сервер сами, а вызовы из обычных классов - нет)
//    public void stopServer() {
//        System.out.println("Останавливаем сервер");
//        httpServer.stop(1);
//    }

} // end of class

