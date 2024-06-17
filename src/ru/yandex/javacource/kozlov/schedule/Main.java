package ru.yandex.javacource.kozlov.schedule;

import ru.yandex.javacource.kozlov.schedule.manager.task.FileBackedTaskManager;
import ru.yandex.javacource.kozlov.schedule.manager.task.TaskManager;
import ru.yandex.javacource.kozlov.schedule.task.Epic;
import ru.yandex.javacource.kozlov.schedule.task.Subtask;
import ru.yandex.javacource.kozlov.schedule.task.Task;
import ru.yandex.javacource.kozlov.schedule.task.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    private static TaskManager taskManager;

    public static void main(String[] args) {
        System.out.println("Поехали!");
        taskManager = FileBackedTaskManager.restoreFromFile(new File("resources/task.csv"));

        Task firstTask = createTask("firstTask", "firstDescription", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(1));
        Task secondTask = createTask("secondTask", "secondDescription", TaskStatus.IN_PROGRESS, LocalDateTime.now().plusDays(1), Duration.ofMinutes(20));

        Epic firstEpic = createEpic("firstEpic", "Epic description");
        Epic secondEpic = createEpic("secondEpic", "Epic description");

        Subtask firstSubtask = createSubtask("firstSubtask", "Subtask description", TaskStatus.NEW, firstEpic.getId(), LocalDateTime.now().plusDays(2), Duration.ofMinutes(5));
        Subtask secondSubtask = createSubtask("secondSubtask", "Subtask description", TaskStatus.IN_PROGRESS, firstEpic.getId(), LocalDateTime.now().plusDays(3), Duration.ofMinutes(10));
        Subtask thirdSubtask = createSubtask("thirdSubtask", "Subtask", TaskStatus.DONE, secondEpic.getId(), LocalDateTime.now().plusDays(4), Duration.ofMinutes(11));
        printAllTasks("AfterCreating");

        updateStatus(firstTask, TaskStatus.IN_PROGRESS);
        updateStatus(secondTask, TaskStatus.DONE);
        updateStatus(firstSubtask, TaskStatus.DONE);
        updateStatus(secondSubtask, TaskStatus.DONE);
        updateStatus(thirdSubtask, TaskStatus.NEW);
        printAllTasks("AfterUpdating");

        taskManager.removeTask(firstTask.getId());
        taskManager.removeEpic(firstEpic.getId());
        printAllTasks("AfterRemoving");

        printAllTasks(taskManager);

        userScenario();
    }

    private static Subtask createSubtask(String name, String description, TaskStatus taskStatus, int epicId, LocalDateTime startTime, Duration duration) {
        Subtask subtask = new Subtask(name, description, taskStatus, epicId, startTime, duration);
        taskManager.createSubtask(subtask);
        return subtask;
    }

    private static Task createTask(String name, String description, TaskStatus taskStatus, LocalDateTime startTime, Duration duration) {
        Task task = new Task(name, description, taskStatus, startTime, duration);
        taskManager.createTask(task);
        return task;
    }

    private static Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description);
        taskManager.createEpic(epic);
        return epic;
    }

    private static void updateStatus(Task task, TaskStatus status) {
        task.setTaskStatus(status);
        if (task.getClass() == Task.class) {
            taskManager.updateTask(task);
        } else if (task.getClass() == Subtask.class) {
            taskManager.updateSubtask((Subtask)task);
        }
    }

    private static void printAllTasks(String action) {
        System.out.println();
        System.out.println(action);
        taskManager.getAllTasks().forEach(task -> System.out.println(task));
        taskManager.getAllEpics().forEach(epic -> System.out.println(epic));
        taskManager.getAllSubtasks().forEach(subtask -> System.out.println(subtask));
    }

    private static void printHistory(String action) {
        System.out.println();
        System.out.println(action);
        taskManager.getHistory().forEach(task -> System.out.println(task));
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void userScenario() {
        Task firstTask = createTask("firstTask", "firstDescription", TaskStatus.NEW, LocalDateTime.now().minusDays(5), Duration.ofMinutes(5));
        Task secondTask = createTask("secondTask", "secondDescription", TaskStatus.IN_PROGRESS, LocalDateTime.now().minusDays(4), Duration.ofMinutes(15));

        Epic firstEpic = createEpic("firstEpic", "Epic description");
        Epic secondEpic = createEpic("secondEpic", "Epic description");

        Subtask firstSubtask = createSubtask("firstSubtask", "Subtask description", TaskStatus.NEW, firstEpic.getId(), LocalDateTime.now().minusDays(3), Duration.ofMinutes(20));
        Subtask secondSubtask = createSubtask("secondSubtask", "Subtask description", TaskStatus.IN_PROGRESS, firstEpic.getId(), LocalDateTime.now().minusDays(2), Duration.ofMinutes(1));
        Subtask thirdSubtask = createSubtask("thirdSubtask", "Subtask", TaskStatus.DONE, firstEpic.getId(), LocalDateTime.now().minusDays(1), Duration.ofMillis(50000));
        printAllTasks("UserScenario -- AfterCreating");

        taskManager.getTask(secondTask.getId());
        taskManager.getSubtask(secondSubtask.getId());
        taskManager.getEpic(firstEpic.getId());
        taskManager.getTask(secondTask.getId());
        taskManager.getTask(firstTask.getId());
        taskManager.getEpic(secondEpic.getId());
        taskManager.getEpic(firstEpic.getId());
        taskManager.getSubtask(firstSubtask.getId());
        taskManager.getSubtask(secondSubtask.getId());
        taskManager.getSubtask(thirdSubtask.getId());
        taskManager.getEpic(secondEpic.getId());
        taskManager.getTask(firstTask.getId());
        taskManager.getSubtask(secondSubtask.getId());
        taskManager.getEpic(secondEpic.getId());
        taskManager.getTask(secondTask.getId());
        taskManager.getSubtask(thirdSubtask.getId());
        printHistory("UserScenario -- AfterGettingTasks");

        taskManager.removeTask(firstTask.getId());
        printHistory("UserScenario -- AfterDeletingTask");

        taskManager.removeEpic(firstEpic.getId());
        printHistory("UserScenario -- AfterDeletingEpic");
    }
}
