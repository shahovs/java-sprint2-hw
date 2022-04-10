package manager;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final int MAX_TASKS_IN_HISTORY = 10;
    private final LinkedList<Task> history;

    public InMemoryHistoryManager() {
        history = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        if (history.size() == MAX_TASKS_IN_HISTORY) {
            history.remove();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

}
