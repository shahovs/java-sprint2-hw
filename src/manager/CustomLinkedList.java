/*
package manager;
import java.util.List;
import java.util.ArrayList;

public class CustomLinkedList<T> {
    Node<T> head;
    Node<T> tail;

    public Object linkLast(T t) {
        Node<T> node = new Node<>(t);
        if (head == null) {
            head = node;
            tail = node;
        } else if (head == tail) {
            head.next = node;
            tail = node;
            tail.prev = head;
        } else {
            Node<T> oldLast = tail;
            oldLast.next = node;
            tail = node;
            tail.prev = oldLast;
        }
        return node;
    }

    public void removeNode(Object link) {
        if (head == null) {
            return;
        }
        Node<T> node = (Node<T>) link;
        if (head == tail && head == link) {
            head = null;
            tail = null;
        } else {
            Node<T> prevNode = node.prev;
            Node<T> nextNode = node.next;
            if (prevNode != null) {
                prevNode.next = nextNode;
            }
            if (nextNode != null) {
                nextNode.prev = prevNode;
            }
        }
    }

    public List<T> getTasks() {
        List<T> list = new ArrayList<>();
        Node<T> node = head;
        while(node!=null) {
            list.add(node.t);
            node = node.next;
        }
        return list;
    }

    @Override
    public String toString() {
        String result = "\n*****CustomLinkedList:*********************************************************";
        Node node = head;
        while (node != null) {
            result += node;
            node = node.next;
        }
        return result;
    }
}

class Node<T> {
    Node next;
    Node prev;
    T t;

    public Node(T t) {
        this.t = t;
    }

    @Override
    public String toString() {
        return "" + t;
    }
}
*/
