package manager;

import model.Task;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> historyById;
    private Node head;
    private Node tail;

    static class Node {
        Node prev;
        Node next;
        Task task;

        public Node(Task task) {
            this.task = task;
            prev = null;
            next = null;
        }

        @Override
        public String toString() {
            return "" + task;
        }
    }

    public InMemoryHistoryManager() {
        historyById = new HashMap<>();
        head = null;
        tail = null;
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            Node oldLast = tail;
            oldLast.next = newNode;
            tail = newNode;
            newNode.prev = oldLast;
        }
        historyById.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        if (!historyById.containsKey(id)) {
            return;
        }
        Node link = historyById.get(id);
        historyById.remove(id);
        if (link == null) {
            return;
        }
        removeNode(link);
    }

    private void removeNode(Node link) {
        if (head == null) {
            return;
        }
        if (head == tail) { // Если есть только один элемент (первый, он же последний, его же удаляем)
            head = null;
            tail = null;
            return;
        }
        if (link == head) { // Если удаляем первый элемент
            head = head.next;
            head.prev = null;
            return;
        }
        if (link == tail) { // Если удаляем последний элемент
            tail = tail.prev;
            tail.next = null;
            return;
        }
        Node prevNode = link.prev; // В остальных случаях (удаляем элемент из середины списка)
        Node nextNode = link.next;
        if (prevNode != null) {
            prevNode.next = nextNode;
        }
        if (nextNode != null) {
            nextNode.prev = prevNode;
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        Node node = head;
        while (node != null) {
            if (node.task != null)
                history.add(node.task);
            node = node.next;
        }
        return history;
    }

}
