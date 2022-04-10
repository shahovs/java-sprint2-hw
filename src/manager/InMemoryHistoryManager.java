package manager;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final int MAX_TASKS_IN_HISTORY = 10;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        history.add(task);
        if (history.size() > MAX_TASKS_IN_HISTORY) {
            history.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

}
