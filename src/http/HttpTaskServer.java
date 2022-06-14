package http;

import java.net.InetSocketAddress;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import manager.Managers;
import manager.TaskManager;
import model.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.TreeSet;

public class HttpTaskServer {

    private final TaskManager manager = Managers.getDefault();
    private static final int PORT = 8080;
    private final static String uriStart = "http://localhost:" + PORT;

    private final static String pathBeginOnly = "/tasks/";
    private final static String pathTask = "/tasks/task/";
    private final static String pathSubtask = "/tasks/subtask/";
    private final static String pathEpic = "/tasks/epic/";
    private final static String pathHistory = "/tasks/history";
    private final static String pathPrioritized = "/tasks/prioritized";

    public final static String uriHeadOnly = uriStart + pathBeginOnly;
    public final static String uriTask = uriStart + pathTask;
    public final static String uriSubtask = uriStart + pathSubtask;
    public final static String uriEpic = uriStart + pathEpic;
    public final static String uriHistory = uriStart + pathHistory;
    public final static String uriPrioritized = uriStart + pathPrioritized;

    public final static String pathIdComponent = "?id=";


    public void runServer() {
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
    class tasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if (!"GET".equals(httpExchange.getRequestMethod())) {
                send404AndClose(httpExchange);
            }
            httpExchange.sendResponseHeaders(200, 0);
            List<Task> allTasks = manager.getAllTasks();
            List<Subtask> allSubtasks = manager.getAllSubtasks();
            List<Epic> allEpics = manager.getAllEpics();
            List<Task> all = allTasks;
            all.addAll(allSubtasks);
            all.addAll(allEpics);
            Gson gson = new Gson();
            String json = gson.toJson(all);
            sendBody(httpExchange, json);
        }
    }

    private void prioritizedHandle(HttpExchange httpExchange) throws IOException {
        if (!"GET".equals(httpExchange.getRequestMethod())) {
            send404AndClose(httpExchange);
        }
        httpExchange.sendResponseHeaders(200, 0);
        TreeSet<Task> prioritizedTasks = manager.getPrioritizedTasks();
        Gson gson = new Gson();
        String json = gson.toJson(prioritizedTasks);
        sendBody(httpExchange, json);
    }

    private void historyHandle(HttpExchange httpExchange) throws IOException {
        if (!"GET".equals(httpExchange.getRequestMethod())) {
            send404AndClose(httpExchange);
        }
        httpExchange.sendResponseHeaders(200, 0);
        List<Task> history = manager.getHistory();
        Gson gson = new Gson();
        String json = gson.toJson(history);
        sendBody(httpExchange, json);
    }

    private void taskHandle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        URI uri = httpExchange.getRequestURI();
        String uriString = uri.toString();
        //System.out.println("uriString: " + uriString);
        String pathWithIdComponent = pathTask + pathIdComponent;
        //System.out.println("pathWithIdComponent: " + pathWithIdComponent);
        if (uriString.startsWith(pathWithIdComponent)) {
            String idString = uriString.substring(pathWithIdComponent.length());
            try {
                int id = Integer.parseInt(idString);
            } catch (NumberFormatException e) {
                send404AndClose(httpExchange);
                e.printStackTrace();
                return;
            }
            switch (method) {
                case "GET":
                    System.out.println("Тест должен оказаться здесь");
                    break;
                case "DELETE":
                    break;
                default:
                    send404AndClose(httpExchange); // или унести в конец метода
            }
        }

        switch (method) {
            case "GET":
                String uriStr = uri.toString();
                //System.out.println("uriS= " + uriStr); // /tasks/task/?id=1
                String path = uri.getPath();
                //System.out.println("path= " + path); // /tasks/task/
                break;
            case "DELETE":
                break;
            case "POST":
                break;
            case "PUT":
                break;
            default:
                send404AndClose(httpExchange); // или унести в конец метода
        }

        httpExchange.sendResponseHeaders(200, 0);
        List<Task> history = manager.getHistory();
        Gson gson = new Gson();
        String json = gson.toJson(history);
        sendBody(httpExchange, json);
    }

    private void subtaskHandle(HttpExchange httpExchange) throws IOException {
        if (!"POST".equals(httpExchange.getRequestMethod())) {
            send404AndClose(httpExchange);
        }
        httpExchange.sendResponseHeaders(200, 0);
        List<Task> history = manager.getHistory();
        Gson gson = new Gson();
        String json = gson.toJson(history);
        sendBody(httpExchange, json);
    }

    private void epicHandle(HttpExchange httpExchange) throws IOException {
        if (!"GET".equals(httpExchange.getRequestMethod())) {
            send404AndClose(httpExchange);
        }
        httpExchange.sendResponseHeaders(200, 0);
        List<Task> history = manager.getHistory();
        Gson gson = new Gson();
        String json = gson.toJson(history);
        sendBody(httpExchange, json);
    }

    private void send404AndClose(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(404, 0);
        httpExchange.close();
    }

    private void sendBody(HttpExchange httpExchange, String responseBody) throws IOException {
        OutputStream os = httpExchange.getResponseBody();
        os.write(responseBody.getBytes());
        httpExchange.close();
    }


//     Метод не работает. Как остановить сервер?
//     (тесты умеют останавливать сервер сами, а вызовы из обычных классов - нет)
//    public void stopServer() {
//        System.out.println("Останавливаем сервер");
//        httpServer.stop(1);
//    }

} // end of class

