package ru.yandex.javacource.kozlov.schedule.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kozlov.schedule.manager.history.HistoryManager;
import ru.yandex.javacource.kozlov.schedule.manager.task.TaskManager;

@DisplayName("Managers")
public class ManagersTest {
    @Test
    @DisplayName("Should create ready task manager")
    public void shouldCreateReadyTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        Assertions.assertNotNull(taskManager, "taskManager is null");
    }

    @Test
    @DisplayName("Should create ready history manager")
    public void shouldCreateReadyHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(historyManager, "historyManager is null");
    }
}
