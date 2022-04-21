package manager;

import model.Task;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> history;
    private final Map<Integer, Object> historyById;


    public InMemoryHistoryManager() {
        history = new CustomLinkedList<>();
        historyById = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        int id = task.getId();
        remove(id);
        Object newLink = history.linkLast(task);
        historyById.put(id, newLink);
    }

    @Override
    public void remove(int id) {
        Object link = historyById.get(id);
        if (link != null) {
            history.removeNode(link);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

}
