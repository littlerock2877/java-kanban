package ru.yandex.javacource.kozlov.schedule.manager.task;

import ru.yandex.javacource.kozlov.schedule.task.Epic;
import ru.yandex.javacource.kozlov.schedule.task.Subtask;
import ru.yandex.javacource.kozlov.schedule.task.Task;
import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtask(int id);

    List<Subtask> getEpicSubtasks(Epic epic);

    List<Task> getHistory();
    List<Task> getPrioritizedTasks();
}
