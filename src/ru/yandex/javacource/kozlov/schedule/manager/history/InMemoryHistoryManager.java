package ru.yandex.javacource.kozlov.schedule.manager.history;

import ru.yandex.javacource.kozlov.schedule.task.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_SIZE = 10;

    private final List<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>(HISTORY_SIZE);
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() >= HISTORY_SIZE) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
