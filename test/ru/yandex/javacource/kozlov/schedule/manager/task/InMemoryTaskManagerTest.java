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

@DisplayName("InMemoryTaskManagerTest")
public class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    @DisplayName("Should not task change after adding to history")
    public void shouldNotTaskChangeAfterAddingToHistory() {
        Task task = new Task("task", "description", TaskStatus.NEW);
        taskManager.createTask(task);
        taskManager.getHistory().add(task);
        assertTasksEquals(task, taskManager.getHistory().getFirst());
    }

    @Test
    @DisplayName("Should not change task after adding to taskManager")
    public void shouldNotTaskChangeAfterAddingToManager() {
        Task task = new Task("task", "description", TaskStatus.NEW);
        taskManager.createTask(task);
        assertTasksEquals(task, taskManager.getAllTasks().getFirst());
    }

    @Test
    @DisplayName("Could add a task to manager with existing id")
    public void couldAddTaskToManagerWithExistingId() {
        Task task = new Task("task", "description", TaskStatus.NEW);
        taskManager.createTask(task);
        Task firstTask = taskManager.getAllTasks().getFirst();
        Task task2 = new Task("task", "description", TaskStatus.NEW);
        task2.setId(firstTask.getId());
        taskManager.createTask(task2);
        Assertions.assertTrue(taskManager.getAllTasks().contains(task));
        Assertions.assertTrue(taskManager.getAllTasks().contains(task2));
    }

    @Test
    @DisplayName("Could add and find different types of tasks")
    public void couldAddAndFindDifferentTypesOfTasks() {
        Task task = new Task("task", "description", TaskStatus.NEW);
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtask", "description", TaskStatus.DONE, epic.getId());
        taskManager.createTask(task);
        taskManager.createSubtask(subtask);
        Assertions.assertNotNull(taskManager.getTask(taskManager.getAllTasks().getFirst().getId()));
        Assertions.assertNotNull(taskManager.getEpic(taskManager.getAllEpics().getFirst().getId()));
        Assertions.assertNotNull(taskManager.getSubtask(taskManager.getAllSubtasks().getFirst().getId()));
    }

    public void assertTasksEquals(Task expectedTask, Task currentTask) {
        Assertions.assertEquals(expectedTask.getId(), currentTask.getId(), "expectedTask.getId() != currentTask.getId()");
        Assertions.assertEquals(expectedTask.getName(), currentTask.getName(), "expectedTask.getName() != currentTask.getName()");
        Assertions.assertEquals(expectedTask.getDescription(), currentTask.getDescription(), "expectedTask.getDescription() != currentTask.getDescription()");
        Assertions.assertEquals(expectedTask.getTaskStatus(), currentTask.getTaskStatus(), "expectedTask.getTaskStatus() != currentTask.getTaskStatus()");
    }
}
