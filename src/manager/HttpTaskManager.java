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
        load();
    }

    // Обращается к KVTaskClient, чтобы загрузить задачи (состояние менеджера) из KVServer
    @Override
    protected void load() {
//        tasksFromServer = gson.fromJson(json, List.class);
        System.out.println("\nHttpTaskManager /load");

        String json = kvTaskClient.load(KEY_TASKS);
        List<Task> tasksFromServer;
        tasksFromServer = gson.fromJson(json, new TypeToken<ArrayList<Task>>() {
        }.getType());
        System.out.println("HttpTaskManager.load() fromJson (tasksFromServer):\n" + tasksFromServer);
        if (tasksFromServer != null) {
            for (Task task : tasksFromServer) {
                int id = task.getId();
                tasks.put(id, task);
            }
        }

        json = kvTaskClient.load(KEY_SUBTASKS);
        List<Subtask> subtasksFromServer;
        subtasksFromServer = gson.fromJson(json, new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        System.out.println("HttpTaskManager.load() fromJson (subtasksFromServer ):\n" + subtasksFromServer);
        if (subtasksFromServer != null) {
            for (Subtask subtask : subtasksFromServer) {
                int id = subtask.getId();
                tasks.put(id, subtask);
            }
        }

        json = kvTaskClient.load(KEY_EPICS);
        List<Epic> epicsFromServer;
        epicsFromServer = gson.fromJson(json, new TypeToken<ArrayList<Epic>>() {
        }.getType());
        System.out.println("HttpTaskManager.load() fromJson (epicsFromServer ):\n" + epicsFromServer);
        if (epicsFromServer != null) {
            for (Epic epic : epicsFromServer) {
                int id = epic.getId();
                tasks.put(id, epic);
            }
        }

        json = kvTaskClient.load(KEY_HISTORY);
        List<Task> history;
        history = gson.fromJson(json, new TypeToken<ArrayList<Task>>() {
        }.getType());
        System.out.println("HttpTaskManager.load() fromJson (history):\n" + history);
        if (history != null) {
            for (Task task : history) {
                historyManager.add(task);
            }
        }
    }

    // Обращается к KVTaskClient, чтобы тот сохранил состояние менеджера в KVServer
    @Override
    protected void save() {

        List<Task> allTasks = getAllTasks();
        String json = gson.toJson(allTasks);
        System.out.println("\nHttpTaskManager.save() json (allTasks):\n" + json);
        kvTaskClient.put(KEY_TASKS, json);

        List<Subtask> allSubtasks = getAllSubtasks();
        json = gson.toJson(allSubtasks);
        System.out.println("\nHttpTaskManager.save() json (allSubtasks):\n" + json);
        kvTaskClient.put(KEY_SUBTASKS, json);

        List<Epic> allEpics = getAllEpics();
        json = gson.toJson(allEpics);
        System.out.println("\nHttpTaskManager.save() json (allEpics):\n" + json);
        kvTaskClient.put(KEY_EPICS, json);

        List<Task> history = getHistory();
        json = gson.toJson(history);
        System.out.println("HttpTaskManager.save() json (history):\n" + json);
        kvTaskClient.put(KEY_HISTORY, json);
    }

}
