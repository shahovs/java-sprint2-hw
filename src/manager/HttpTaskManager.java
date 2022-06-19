package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.EpicAdapter;
import http.KVTaskClient;
import http.LocalDateTimeAdapter;
import http.SubtaskAdapter;
import manager.FileBackedTaskManager;
import model.*;

import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager {

    private final String KEY_TASKS = "tasks";
    private final String KEY_SUBTASKS = "subtasks";
    private final String KEY_EPICS = "epics";
    private final String KEY_HISTORY = "history";

    private final KVTaskClient kvTaskClient;
    private final Gson gson;

//    public HTTPTaskManager(String path) {
//        super(path);
//    }

    public HttpTaskManager(String uri) {
        super("");
        kvTaskClient = new KVTaskClient(uri);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                //.registerTypeAdapter(Subtask.class, new SubtaskAdapter())
                .setPrettyPrinting()
                .create();
        // TODO После создания клиента менеджер запрашивает у него исходное состояние менеджера
    }

    // Обращается к KVTaskClient, чтобы загрузить задачи (состояние менеджера) из KVServer
    @Override
    protected void load() {
        String jsonT = kvTaskClient.load(KEY_TASKS);
        List<Task> tasks;
        tasks = gson.fromJson(jsonT, List.class);
        System.out.println("HttpTaskManager.load() fromJson (tasks):\n" + tasks);
        String jsonS = kvTaskClient.load(KEY_SUBTASKS);

        String jsonE = kvTaskClient.load(KEY_EPICS);

        String jsonH = kvTaskClient.load(KEY_HISTORY);

    }

    // Обращается к KVTaskClient, чтобы тот сохранил состояние менеджера в KVServer
    @Override
    protected void save() /*throws ManagerSaveException*/ {

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
