package manager;

import java.io.File;

public class Managers {

    private static final InMemoryHistoryManager historyManager;

    static {
        historyManager = new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new FileBackedTasksManager(new File("tasks.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}