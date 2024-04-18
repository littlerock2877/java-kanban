package ru.yandex.javacource.kozlov.schedule.manager.historyManager;

import ru.yandex.javacource.kozlov.schedule.task.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> viewedTasks;

    private final int HISTORY_SIZE = 10;

    public InMemoryHistoryManager() {
        viewedTasks = new ArrayList<>(HISTORY_SIZE);
    }
    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (viewedTasks.size() >= HISTORY_SIZE) {
            viewedTasks.remove(0);
        }
        viewedTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return viewedTasks;
    }
}
