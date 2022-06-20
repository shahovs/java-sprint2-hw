package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import http.KVTaskClient;
import http.LocalDateTimeAdapter;
import model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager {

    private final String KEY_TASKS = "tasks";
    private final String KEY_SUBTASKS = "subtasks";
    private final String KEY_EPICS = "epics";
    private final String KEY_HISTORY = "history";

    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager(String uri) {
        super("");
        kvTaskClient = new KVTaskClient(uri);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    // Обращается к KVTaskClient, чтобы загрузить задачи (состояние менеджера) из KVServer
    @Override
    protected void load() {
//        tasks = gson.fromJson(json, List.class);
        String json = kvTaskClient.load(KEY_TASKS);
        List<Task> tasks;
        tasks = gson.fromJson(json, new TypeToken<ArrayList<Task>>() {
        }.getType());
        // TODO Возможно, вместо List<Task> нужен ArrayList<Task> или наоборот (проверить)
        System.out.println("HttpTaskManager.load() fromJson (tasks):\n" + tasks);
        for (Task task : tasks) {
            createTask(task);
        }

        json = kvTaskClient.load(KEY_SUBTASKS);
        List<Subtask> subtasks;
        subtasks = gson.fromJson(json, new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        System.out.println("HttpTaskManager.load() fromJson (subtasks):\n" + subtasks);
        for (Subtask subtask : subtasks) {
            createTask(subtask);
        }

        json = kvTaskClient.load(KEY_EPICS);
        List<Epic> epics;
        epics = gson.fromJson(json, new TypeToken<ArrayList<Epic>>() {
        }.getType());
        System.out.println("HttpTaskManager.load() fromJson (epics):\n" + epics);
        for (Epic epic : epics) {
            createTask(epic);
        }

        json = kvTaskClient.load(KEY_HISTORY);
        List<Task> history;
        history = gson.fromJson(json, new TypeToken<ArrayList<Task>>() {
        }.getType());
        System.out.println("HttpTaskManager.load() fromJson (history):\n" + history);
        for (Task task : history) {
            historyManager.add(task);
        }
    }

    // Обращается к KVTaskClient, чтобы тот сохранил состояние менеджера в KVServer
    @Override
    protected void save() {

        List<Task> allTasks = getAllTasks();
        String json = gson.toJson(allTasks);
        System.out.println("\nHTTPTaskManager.save() json (allTasks):\n" + json);
        kvTaskClient.put(KEY_TASKS, json);

        List<Subtask> allSubtasks = getAllSubtasks();
        json = gson.toJson(allSubtasks);
        System.out.println("\nHTTPTaskManager.save() json (allSubtasks):\n" + json);
        kvTaskClient.put(KEY_SUBTASKS, json);

        List<Epic> allEpics = getAllEpics();
        json = gson.toJson(allEpics);
        System.out.println("\nHTTPTaskManager.save() json (allEpics):\n" + json);
        kvTaskClient.put(KEY_EPICS, json);

        List<Task> history = getHistory();
        json = gson.toJson(history);
        System.out.println("HTTPTaskManager.save() json (history):\n" + json);
        kvTaskClient.put(KEY_HISTORY, json);
    }

}
