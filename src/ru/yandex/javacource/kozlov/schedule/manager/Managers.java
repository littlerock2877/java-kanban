package ru.yandex.javacource.kozlov.schedule.manager;

import ru.yandex.javacource.kozlov.schedule.manager.history.HistoryManager;
import ru.yandex.javacource.kozlov.schedule.manager.history.InMemoryHistoryManager;
import ru.yandex.javacource.kozlov.schedule.manager.task.FileBackedTaskManager;
import ru.yandex.javacource.kozlov.schedule.manager.task.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
