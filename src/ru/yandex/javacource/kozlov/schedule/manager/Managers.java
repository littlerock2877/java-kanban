package ru.yandex.javacource.kozlov.schedule.manager;

import ru.yandex.javacource.kozlov.schedule.manager.historyManager.HistoryManager;
import ru.yandex.javacource.kozlov.schedule.manager.historyManager.InMemoryHistoryManager;
import ru.yandex.javacource.kozlov.schedule.manager.taskManager.InMemoryTaskManager;
import ru.yandex.javacource.kozlov.schedule.manager.taskManager.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
