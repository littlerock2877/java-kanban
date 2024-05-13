package ru.yandex.javacource.kozlov.schedule.manager.history;

import ru.yandex.javacource.kozlov.schedule.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        Node node = history.get(task.getId());
        removeNode(node);
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        removeNode(node);
    }

    private void linkLast(Task task) {
        final Node last = tail;
        final Node newNode = new Node(last, task, null);
        history.put(task.getId(), newNode);
        tail = newNode;
        if (last == null) {
            head = newNode;
        } else {
            last.next = newNode;
        }
    }

    private List<Task> getTasks() {
        List<Task> resultList = new ArrayList<>();
        Node current = head;
        while (current != null) {
            resultList.add(current.data);
            current = current.next;
        }
        return resultList;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        Node next = node.next;
        Node prev = node.prev;
        if (prev == null) {
            head = next;
        } else {
            node.prev.next = next;
        }
        if (next == null) {
            tail = prev;
        } else {
            node.next.prev = prev;
        }
        history.remove(node.data.getId());
    }

    static class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}