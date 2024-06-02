package ru.yandex.javacource.kozlov.schedule.manager.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kozlov.schedule.manager.Managers;
import ru.yandex.javacource.kozlov.schedule.task.Epic;
import ru.yandex.javacource.kozlov.schedule.task.Subtask;
import ru.yandex.javacource.kozlov.schedule.task.Task;
import ru.yandex.javacource.kozlov.schedule.task.TaskStatus;

import static ru.yandex.javacource.kozlov.schedule.manager.task.FileBackedTaskManager.DEFAULT_FILE;

@DisplayName("FileBackedTaskManagerTest")
public class FileBackedTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    @DisplayName("Should manager be reverted from file")
    public void shouldManagerBeRevertedFromFile() {
        Task task1 = new Task("first task", "first task description", TaskStatus.NEW);
        Task task2 = new Task("second task", "second task description", TaskStatus.DONE);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        Epic epic1 = new Epic("first epic", "first epic description");
        Epic epic2 = new Epic("second epic", "second epic description");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask("first subtask", "first subtask description", TaskStatus.IN_PROGRESS, epic2.getId());
        Subtask subtask2 = new Subtask("second subtask", "second subtask description", TaskStatus.DONE, epic2.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        FileBackedTaskManager restoredManager = FileBackedTaskManager.restoreFromFile(DEFAULT_FILE);
        Assertions.assertTrue(restoredManager.getAllTasks().containsAll(taskManager.getAllTasks()));
        Assertions.assertTrue(restoredManager.getAllEpics().containsAll(taskManager.getAllEpics()));
        Assertions.assertTrue(restoredManager.getAllSubtasks().containsAll(taskManager.getAllSubtasks()));
    }
}
