package ru.yandex.javacource.kozlov.schedule.manager.history;

import ru.yandex.javacource.kozlov.schedule.task.Task;
import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();
}
