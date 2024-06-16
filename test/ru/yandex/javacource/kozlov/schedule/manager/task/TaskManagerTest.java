package ru.yandex.javacource.kozlov.schedule.manager.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kozlov.schedule.exception.ValidationException;
import ru.yandex.javacource.kozlov.schedule.task.Epic;
import ru.yandex.javacource.kozlov.schedule.task.Subtask;
import ru.yandex.javacource.kozlov.schedule.task.Task;
import ru.yandex.javacource.kozlov.schedule.task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class TaskManagerTest<T extends TaskManager> {
    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = createManager();
    }

    public abstract T createManager();

    @Test
    @DisplayName("Could task be added to manager")
    public void couldTaskBeAddedToManager() {
        Task task = new Task("task", "description", TaskStatus.NEW, LocalDateTime.now().minusDays(100), Duration.ofSeconds(1));
        taskManager.createTask(task);
        Assertions.assertTrue(taskManager.getAllTasks().contains(task));
    }

    @Test
    @DisplayName("Could epic be added to manager")
    public void couldEpicBeAddedToManager() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Assertions.assertTrue(taskManager.getAllEpics().contains(epic));
    }

    @Test
    @DisplayName("Could subtask be added to manager")
    public void couldSubtaskBeAddedToManager() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtask", "description", TaskStatus.DONE, epic.getId(), LocalDateTime.now().minusDays(99), Duration.ofSeconds(1));
        taskManager.createSubtask(subtask);
        Assertions.assertTrue(taskManager.getAllSubtasks().contains(subtask));
    }

    @Test
    @DisplayName("Could not subtask be added to manager without existing epic")
    public void couldNotSubtaskBeAddedToManagerWithoutExistingEpic() {
        Epic epic = new Epic("epic", "description");
        Subtask subtask = new Subtask("subtask", "description", TaskStatus.DONE, epic.getId(), LocalDateTime.now().minusDays(98), Duration.ofSeconds(1));
        taskManager.createSubtask(subtask);
        Assertions.assertFalse(taskManager.getAllSubtasks().contains(subtask));
    }

    @Test
    @DisplayName("Should correct task be get by id")
    public void shouldCorrectTaskBeGetById() {
        Task task = new Task("task", "description", TaskStatus.NEW, LocalDateTime.now().minusDays(97), Duration.ofSeconds(1));
        taskManager.createTask(task);
        int taskId = taskManager.getAllTasks().stream().filter(t -> t.getName().equals(task.getName())).findFirst().get().getId();
        Task savedTask = taskManager.getTask(taskId);
        assertTasksEquals(task, savedTask);
    }

    @Test
    @DisplayName("Should correct epic be get by id")
    public void shouldCorrectEpicBeGetById() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        int epicId = taskManager.getAllEpics().stream().filter(e -> e.getName().equals(epic.getName())).findFirst().get().getId();
        Epic savedEpic = taskManager.getEpic(epicId);
        assertTasksEquals(epic, savedEpic);
    }

    @Test
    @DisplayName("Should correct subtask be get by id")
    public void shouldCorrectSubtaskBeGetById() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtask", "description", TaskStatus.DONE, epic.getId(), LocalDateTime.now().minusDays(96), Duration.ofSeconds(1));
        taskManager.createSubtask(subtask);
        int subtaskId = taskManager.getAllSubtasks().stream().filter(s -> s.getName().equals(subtask.getName())).findFirst().get().getId();
        Subtask savedSubtask = taskManager.getSubtask(subtaskId);
        assertTasksEquals(subtask, savedSubtask);
    }

    @Test
    @DisplayName("Could task be updated")
    public void couldTaskBeUpdated() {
        Task task = new Task("task", "description", TaskStatus.NEW, LocalDateTime.now().minusDays(95), Duration.ofSeconds(1));
        taskManager.createTask(task);
        int taskId = taskManager.getAllTasks().stream().filter(t -> t.getName().equals(task.getName())).findFirst().get().getId();
        Task update = new Task(taskId, "updated name", "updated description", TaskStatus.DONE, LocalDateTime.now().minusDays(94), Duration.ofSeconds(1));
        taskManager.updateTask(update);
        Task savedTask = taskManager.getTask(taskId);
        assertTasksEquals(update, savedTask);
    }

    @Test
    @DisplayName("Could subtask be updated")
    public void couldSubtaskBeUpdated() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtask", "description", TaskStatus.DONE, epic.getId(), LocalDateTime.now().minusDays(93), Duration.ofSeconds(1));
        taskManager.createSubtask(subtask);
        int subtaskId = taskManager.getAllSubtasks().stream().filter(s -> s.getName().equals(subtask.getName())).findFirst().get().getId();
        Subtask update = new Subtask(subtaskId,"updated name", "updated description", TaskStatus.NEW, epic.getId(), LocalDateTime.now().minusDays(92), Duration.ofSeconds(1));
        taskManager.updateSubtask(update);
        Subtask savedSubtask = taskManager.getSubtask(subtaskId);
        assertTasksEquals(update, savedSubtask);
    }

    @Test
    @DisplayName("Could epic be updated")
    public void couldEpicBeUpdated() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        int epicId = taskManager.getAllEpics().stream().filter(e -> e.getName().equals(epic.getName())).findFirst().get().getId();
        Epic update = new Epic(epicId, "updated name", "updated description");
        taskManager.updateEpic(update);
        Epic savedEpic = taskManager.getEpic(epicId);
        assertTasksEquals(update, savedEpic);
    }

    @Test
    @DisplayName("Could task be removed")
    public void couldTaskBeRemoved() {
        Task task = new Task("task", "description", TaskStatus.NEW, LocalDateTime.now().minusDays(91), Duration.ofSeconds(1));
        taskManager.createTask(task);
        int taskId = taskManager.getAllTasks().stream().filter(t -> t.getName().equals(task.getName())).findFirst().get().getId();
        taskManager.removeTask(taskId);
        Assertions.assertFalse(taskManager.getAllTasks().contains(task));
    }

    @Test
    @DisplayName("Could subtask be removed")
    public void couldSubtaskBeRemoved() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtask", "description", TaskStatus.DONE, epic.getId(), LocalDateTime.now().minusDays(90), Duration.ofSeconds(1));
        taskManager.createSubtask(subtask);
        int subtaskId = taskManager.getAllSubtasks().stream().filter(s -> s.getName().equals(subtask.getName())).findFirst().get().getId();
        taskManager.removeSubtask(subtaskId);
        Assertions.assertFalse(taskManager.getAllSubtasks().contains(subtask));
    }

    @Test
    @DisplayName("Could epic be removed")
    public void couldEpicBeRemoved() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        int epicId = taskManager.getAllEpics().stream().filter(e -> e.getName().equals(epic.getName())).findFirst().get().getId();
        taskManager.removeEpic(epicId);
        Assertions.assertFalse(taskManager.getAllEpics().contains(epic));
    }

    @Test
    @DisplayName("Should calculate epic status with new subtasks correctly")
    public void shouldCalculateEpicStatusWithNewSubtasksCorrectly() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "description",TaskStatus.NEW, epic.getId(), LocalDateTime.now().minusDays(89), Duration.ofSeconds(1));
        Subtask subtask2 = new Subtask("subtask2", "description",TaskStatus.NEW, epic.getId(), LocalDateTime.now().minusDays(88), Duration.ofSeconds(1));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Assertions.assertEquals(epic.getTaskStatus(), TaskStatus.NEW);
    }

    @Test
    @DisplayName("Should calculate epic status with done subtasks correctly")
    public void shouldCalculateEpicStatusWithDoneSubtasksCorrectly() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "description",TaskStatus.DONE, epic.getId(), LocalDateTime.now().minusDays(89), Duration.ofSeconds(1));
        Subtask subtask2 = new Subtask("subtask2", "description",TaskStatus.DONE, epic.getId(), LocalDateTime.now().minusDays(88), Duration.ofSeconds(1));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Assertions.assertEquals(epic.getTaskStatus(), TaskStatus.DONE);
    }

    @Test
    @DisplayName("Should calculate epic status with done and new subtasks correctly")
    public void shouldCalculateEpicStatusWithDoneAndNewSubtasksCorrectly() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "description",TaskStatus.NEW, epic.getId(), LocalDateTime.now().minusDays(89), Duration.ofSeconds(1));
        Subtask subtask2 = new Subtask("subtask2", "description",TaskStatus.DONE, epic.getId(), LocalDateTime.now().minusDays(88), Duration.ofSeconds(1));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Assertions.assertEquals(epic.getTaskStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should calculate epic status with in progress subtasks correctly")
    public void shouldCalculateEpicStatusWithInProgressAndNewSubtasksCorrectly() {
        Epic epic = new Epic("epic", "description");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "description",TaskStatus.IN_PROGRESS, epic.getId(), LocalDateTime.now().minusDays(89), Duration.ofSeconds(1));
        Subtask subtask2 = new Subtask("subtask2", "description",TaskStatus.IN_PROGRESS, epic.getId(), LocalDateTime.now().minusDays(88), Duration.ofSeconds(1));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        Assertions.assertEquals(epic.getTaskStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should exception be thrown if tasks are intersect")
    public void shouldExceptionBeThrownIfTasksAreIntersect() {
        Task task1 = new Task("task1", "description1", TaskStatus.NEW, LocalDateTime.now().minusSeconds(10), Duration.ofSeconds(50));
        Task task2 = new Task("task2", "description2", TaskStatus.NEW, LocalDateTime.now(), Duration.ofSeconds(30));
        taskManager.createTask(task1);
        Assertions.assertThrows(ValidationException.class, () -> taskManager.createTask(task2));
    }

    @Test
    @DisplayName("Should not exception be thrown if tasks are not intersect")
    public void shouldNotExceptionBeThrownIfTasksAreNotIntersect() {
        Task task1 = new Task("task1", "description1", TaskStatus.NEW, LocalDateTime.now().minusSeconds(10), Duration.ofSeconds(5));
        Task task2 = new Task("task2", "description2", TaskStatus.NEW, LocalDateTime.now(), Duration.ofSeconds(5));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
    }

    public void assertTasksEquals(Task expectedTask, Task currentTask) {
        Assertions.assertEquals(expectedTask.getName(), currentTask.getName(), "expectedTask.getName() != currentTask.getName()");
        Assertions.assertEquals(expectedTask.getDescription(), currentTask.getDescription(), "expectedTask.getDescription() != currentTask.getDescription()");
        Assertions.assertEquals(expectedTask.getTaskStatus(), currentTask.getTaskStatus(), "expectedTask.getTaskStatus() != currentTask.getTaskStatus()");
        Assertions.assertEquals(expectedTask.getStartTime(), currentTask.getStartTime(), "expectedTask.getStartTime() != currentTask.getStartTime()");
        Assertions.assertEquals(expectedTask.getDuration(), currentTask.getDuration(), "expectedTask.getDuration() != currentTask.getDuration()");
        Assertions.assertEquals(expectedTask.getEndTime(), currentTask.getEndTime(), "expectedTask.getEndTime() != currentTask.getEndTime()");
    }
}
