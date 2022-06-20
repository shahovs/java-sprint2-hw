package http;

import java.net.InetSocketAddress;
import java.net.URI;

import com.google.gson.*;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import manager.HttpTaskManager;
import manager.TaskManager;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import java.time.LocalDateTime;

import java.util.List;
import java.util.TreeSet;


public class HttpTaskServer {
    public static final int PORT = 8080;
    public final static String PATH_BEGIN_ONLY = "/tasks";
    public final static String TASKS_TASK = "/tasks/task";
    public final static String TASKS_SUBTASK = "/tasks/subtask";
    public final static String TASKS_EPIC = "/tasks/epic";
    public final static String TASKS_HISTORY = "/tasks/history";
    public final static String TASKS_PRIORITIZED = "/tasks/prioritized";
    public final static String ID_ATTRIBUTE = "id=";

    private final Gson gson;
    private final TaskManager manager;
    private HttpServer httpServer;

    public HttpTaskServer() {
        this(new HttpTaskManager("http://localhost:" + KVServer.PORT));
    }

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext(PATH_BEGIN_ONLY, new tasksHandler()); // сделал такой вариант для примера
            httpServer.createContext(TASKS_PRIORITIZED, this::prioritizedHandle);
            httpServer.createContext(TASKS_HISTORY, this::historyHandle);
            httpServer.createContext(TASKS_TASK, this::taskHandle);
            httpServer.createContext(TASKS_SUBTASK, this::subtaskHandle);
            httpServer.createContext(TASKS_EPIC, this::epicHandle);
            System.out.println("Запускаем HttpTaskServer на порту " + PORT);
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
                System.out.println("/tasks/ ждёт GET-запрос, а получил: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
                httpExchange.close();
                return;
            }
            try {
                System.out.println("\nHttpTaskServer tasksHandler.handle() RequestURI: " + httpExchange.getRequestURI());

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
            System.out.println("/tasks/prioritized ждёт GET-запрос, а получил: " + httpExchange.getRequestMethod());
            httpExchange.sendResponseHeaders(405, 0);
            httpExchange.close();
            return;
        }
        try {
            System.out.println("\nHttpTaskServer prioritizedHandle() RequestURI: " + httpExchange.getRequestURI());

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
            System.out.println("/tasks/history ждёт GET-запрос, а получил: " + httpExchange.getRequestMethod());
            httpExchange.sendResponseHeaders(405, 0);
            httpExchange.close();
            return;
        }
        try {
            System.out.println("\nHttpTaskServer historyHandle() RequestURI: " + httpExchange.getRequestURI());

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
            System.out.println("\nHttpTaskServer taskHandle() RequestURI: " + httpExchange.getRequestURI());
            String method = httpExchange.getRequestMethod();
            boolean hasIdQuery = checkIdQuery(httpExchange);
            if (hasIdQuery) {
                // path: "/tasks/task/?id=.."
                int id = getId(httpExchange);
                if (id < 0) {
                    System.out.println("/tasks/task id меньше нуля или в нечитаемом формате");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                switch (method) {
                    case "GET":
                        Task task = manager.getTask(id);
                        String json = gson.toJson(task);
                        System.out.println("getTask(id) json\n" + json);
                        httpExchange.sendResponseHeaders(200, 0);
                        sendBodyAndClose(httpExchange, json);
                        break;
                    case "DELETE":
                        manager.removeTask(id);
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/tasks/task/?id= ждёт GET или DELETE-запрос, а получил: " +
                                httpExchange.getRequestMethod());
                        httpExchange.sendResponseHeaders(405, 0);
                }
            } else {
                // if uri has NO id (path: "/tasks/task")
                switch (method) {
                    case "GET":
                        List<Task> allTasks = manager.getAllTasks();
                        String json = gson.toJson(allTasks);
                        System.out.println("getAllTasks() json\n" + json); // удалить после окончания разработки
                        httpExchange.sendResponseHeaders(200, 0);
                        sendBodyAndClose(httpExchange, json);
                        break;
                    case "DELETE":
                        manager.removeAllTasks();
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    case "POST":
                        Task newTask = getTask(httpExchange);
                        if (newTask == null) {
                            System.out.println("/tasks/task POST Задача не прочитана (== null)");
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        manager.createTask(newTask);
                        httpExchange.sendResponseHeaders(200, 0); // подумать о кодах ответа
                        break;
                    case "PUT":
                        Task updatedTask = getTask(httpExchange);
                        if (updatedTask == null) {
                            System.out.println("/tasks/task PUT Задача не прочитана (== null)");
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        manager.updateTask(updatedTask);
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/tasks/task ждёт GET/DELETE/POST или PUT-запрос, а получил: " +
                                httpExchange.getRequestMethod());
                        httpExchange.sendResponseHeaders(405, 0);
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
        if (queryComponent != null && queryComponent.startsWith(ID_ATTRIBUTE)) {
            return true;
        }
        return false;
    }

    private int getId(HttpExchange httpExchange) {
        URI uri = httpExchange.getRequestURI();
        String queryComponent = uri.getQuery(); // "id=1" or null
        int id = -1;
        if (queryComponent != null && queryComponent.startsWith(ID_ATTRIBUTE)) {
            String idString = queryComponent.substring(ID_ATTRIBUTE.length());
            try {
                id = Integer.parseInt(idString);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }
        return id;
    }

    private Task getTask(HttpExchange httpExchange) {
        JsonObject jsonObject = getJsonObject(httpExchange);
        if (jsonObject == null) {
            return null;
        }
        Task task = gson.fromJson(jsonObject, Task.class);
        return task;
    }

    private Subtask getSubtask(HttpExchange httpExchange) {
        JsonObject jsonObject = getJsonObject(httpExchange);
        if (jsonObject == null) {
            return null;
        }
        Subtask subtask = gson.fromJson(jsonObject, Subtask.class);
        return subtask;
    }

    private Epic getEpic(HttpExchange httpExchange) {
        JsonObject jsonObject = getJsonObject(httpExchange);
        if (jsonObject == null) {
            return null;
        }
        Epic epic = gson.fromJson(jsonObject, Epic.class);
        return epic;
    }

    private JsonObject getJsonObject(HttpExchange httpExchange) {
        try (InputStream inputStream = httpExchange.getRequestBody()) {
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("POST/PUT body:\n" + body);
            JsonElement jsonElement = JsonParser.parseString(body);
            if (!jsonElement.isJsonObject()) {
                return null;
            }
            return jsonElement.getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // path: "/tasks/subtask/" or .../?id=..
    private void subtaskHandle(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("\nHttpTaskServer subtaskHandle() RequestURI: " + httpExchange.getRequestURI());
            String method = httpExchange.getRequestMethod();
            boolean hasIdQuery = checkIdQuery(httpExchange);
            if (hasIdQuery) {
                // path: "/tasks/subtask/?id=.."
                int id = getId(httpExchange);
                if (id < 0) {
                    System.out.println("/tasks/subtask id меньше нуля или в нечитаемом формате");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                switch (method) {
                    case "GET":
                        Subtask subtask = manager.getSubtask(id);
                        String json = gson.toJson(subtask);
                        System.out.println("getSubtask(id) json\n" + json);
                        httpExchange.sendResponseHeaders(200, 0);
                        sendBodyAndClose(httpExchange, json);
                        break;
                    case "DELETE":
                        manager.removeSubtask(id);
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/tasks/subtask/?id= ждёт GET или DELETE-запрос, а получил: " +
                                httpExchange.getRequestMethod());
                        httpExchange.sendResponseHeaders(405, 0);
                }
            } else {
                // if uri has NO id (path: "/tasks/subtask")
                switch (method) {
                    case "GET":
                        List<Subtask> allSubtasks = manager.getAllSubtasks();
                        String json = gson.toJson(allSubtasks);
                        System.out.println("getAllSubtasks() json\n" + json); // удалить после окончания разработки
                        httpExchange.sendResponseHeaders(200, 0);
                        sendBodyAndClose(httpExchange, json);
                        break;
                    case "DELETE":
                        manager.removeAllSubtasks();
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    case "POST":
                        Subtask newSubtask = getSubtask(httpExchange);
                        if (newSubtask == null) {
                            System.out.println("/tasks/subtask POST Задача не прочитана (== null)");
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        System.out.println("HttpTaskServer POST Subtask (newSubtask):" + newSubtask);
                        System.out.println("HttpTaskServer POST Subtask (newSubtask' epic):" + newSubtask.getEpic());
                        manager.createSubtask(newSubtask);
                        httpExchange.sendResponseHeaders(200, 0); // подумать о кодах ответа
                        break;
                    case "PUT":
                        Subtask updatedSubtask = getSubtask(httpExchange);
                        if (updatedSubtask == null) {
                            System.out.println("/tasks/subtask PUT Задача не прочитана (== null)");
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        manager.updateSubtask(updatedSubtask);
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/tasks/subtask ждёт GET/DELETE/POST или PUT-запрос, а получил: " +
                                httpExchange.getRequestMethod());
                        httpExchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    // path: "/tasks/epic/" or .../?id=..
    private void epicHandle(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("\nHttpTaskServer epicHandle() RequestURI: " + httpExchange.getRequestURI());
            String method = httpExchange.getRequestMethod();
            boolean hasIdQuery = checkIdQuery(httpExchange);
            if (hasIdQuery) {
                // path: "/tasks/epic/?id=.."
                int id = getId(httpExchange);
                if (id < 0) {
                    System.out.println("/tasks/epic id меньше нуля или в нечитаемом формате");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                switch (method) {
                    case "GET":
                        Epic epic = manager.getEpic(id);
                        String json = gson.toJson(epic);
                        System.out.println("getEpic(id) json\n" + json);
                        httpExchange.sendResponseHeaders(200, 0);
                        sendBodyAndClose(httpExchange, json);
                        break;
                    case "DELETE":
                        manager.removeEpic(id);
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/tasks/epic/?id= ждёт GET или DELETE-запрос, а получил: " +
                                httpExchange.getRequestMethod());
                        httpExchange.sendResponseHeaders(405, 0);
                }
            } else {
                // if uri has NO id (path: "/tasks/epic")
                switch (method) {
                    case "GET":
                        List<Epic> allEpics = manager.getAllEpics();
                        String json = gson.toJson(allEpics);
                        System.out.println("getAllEpics() json\n" + json); // удалить после окончания разработки
                        httpExchange.sendResponseHeaders(200, 0);
                        sendBodyAndClose(httpExchange, json);
                        break;
                    case "DELETE":
                        manager.removeAllEpics();
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    case "POST":
                        Epic newEpic = getEpic(httpExchange);
                        if (newEpic == null) {
                            System.out.println("/tasks/epic POST Задача не прочитана (== null)");
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        manager.createEpic(newEpic);
                        httpExchange.sendResponseHeaders(200, 0); // подумать о кодах ответа
                        break;
                    case "PUT":
                        Epic updatedEpic = getEpic(httpExchange);
                        if (updatedEpic == null) {
                            System.out.println("/tasks/epic PUT Задача не прочитана (== null)");
                            httpExchange.sendResponseHeaders(400, 0);
                            return;
                        }
                        manager.updateEpic(updatedEpic);
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/tasks/epic ждёт GET/DELETE/POST или PUT-запрос, а получил: " +
                                httpExchange.getRequestMethod());
                        httpExchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void sendBodyAndClose(HttpExchange httpExchange, String responseBody) {
        try (OutputStream os = httpExchange.getResponseBody()) {
            byte[] responseBytes = responseBody.getBytes();
            os.write(responseBytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    public void stop() {
        System.out.println("Останавливаем HttpTaskServer");
        httpServer.stop(0);
        System.out.println("HttpTaskServer остановлен");
    }

}

