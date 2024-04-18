package ru.yandex.javacource.kozlov.schedule.manager.historyManager;

import ru.yandex.javacource.kozlov.schedule.task.Task;
import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();
}
