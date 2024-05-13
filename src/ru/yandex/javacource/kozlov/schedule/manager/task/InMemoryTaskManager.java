package ru.yandex.javacource.kozlov.schedule.manager.task;

import ru.yandex.javacource.kozlov.schedule.manager.history.HistoryManager;
import ru.yandex.javacource.kozlov.schedule.task.Epic;
import ru.yandex.javacource.kozlov.schedule.task.Subtask;
import ru.yandex.javacource.kozlov.schedule.task.Task;
import ru.yandex.javacource.kozlov.schedule.task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int counter;
    private HistoryManager historyManager;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Subtask> subtasks;
    private final Map<Integer, Epic> epics;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        this.historyManager = historyManager;
    }

    @Override
    public Task createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        int id = generateId();
        Epic targetEpic = epics.get(subtask.getEpicId());
        if (targetEpic == null) {
            return null;
        }
        subtask.setId(id);
        targetEpic.addSubtask(subtask.getId());
        subtasks.put(id, subtask);
        updateEpicStatus(targetEpic.getId());
        return subtask;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();
        Subtask savedSubtask = subtasks.get(subtaskId);
        if (savedSubtask == null) {
            return;
        }
        int targetEpicId = subtask.getEpicId();
        Epic targetEpic = epics.get(targetEpicId);
        if (targetEpic == null) {
            return;
        }
        subtasks.put(subtaskId, subtask);
        updateEpicStatus(targetEpic.getId());
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();
        Epic savedEpic = epics.get(id);
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        savedEpic.setTaskStatus(epic.getTaskStatus());
        savedEpic.setSubtaskIds(epic.getSubtaskIds());
        epics.put(id, savedEpic);
    }

    @Override
    public void removeAllTasks() {
        tasks.keySet().forEach(task -> historyManager.remove(task));
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        subtasks.keySet().forEach(subtask -> historyManager.remove(subtask));
        subtasks.clear();
        epics.keySet().forEach(epic -> historyManager.remove(epic));
        epics.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            updateEpicStatus(epic.getId());
        }
        subtasks.keySet().forEach(subtask -> historyManager.remove(subtask));
        subtasks.clear();
}

    @Override
    public void removeTask(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        historyManager.remove(id);
        Epic temp = epics.remove(id);
        temp.getSubtaskIds().forEach(subtask -> {
            historyManager.remove(subtask);
            subtasks.remove(subtask);
        });
    }

    @Override
    public void removeSubtask(int id) {
        historyManager.remove(id);
        Subtask temp = subtasks.remove(id);
        if (temp == null) {
            return;
        }
        Epic epic = epics.get(temp.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Integer id : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(id);
            if (subtask != null) {
                result.add(subtask);
            }
        }
        return result;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(int epicId) {
        Epic savedEpic = epics.get(epicId);
        if (savedEpic == null) {
            return;
        }
        List<Subtask> epicSubtasks = getEpicSubtasks(savedEpic);
        if (epicSubtasks.isEmpty()) {
            savedEpic.setTaskStatus(TaskStatus.NEW);
            return;
        }
        var firstSubtaskStatus = epicSubtasks.getFirst().getTaskStatus();
        if (epicSubtasks.stream().allMatch(subtask -> subtask.getTaskStatus() == firstSubtaskStatus)) {
            savedEpic.setTaskStatus(firstSubtaskStatus);
            return;
        }
        savedEpic.setTaskStatus(TaskStatus.IN_PROGRESS);
    }

    private int generateId() {
        return ++counter;
    }
}