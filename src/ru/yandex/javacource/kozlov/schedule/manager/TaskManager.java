package ru.yandex.javacource.kozlov.schedule.manager;

import ru.yandex.javacource.kozlov.schedule.task.Epic;
import ru.yandex.javacource.kozlov.schedule.task.Subtask;
import ru.yandex.javacource.kozlov.schedule.task.Task;
import ru.yandex.javacource.kozlov.schedule.task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int counter;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;

    public TaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public Task createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

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

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public void updateTask(Task task) {
        int id = task.getId();
        Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

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

    public void updateEpic(Epic epic) {
        int id = epic.getId();
        Epic savedEpic = epics.get(id);
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeEpic(int id) {
        Epic temp = epics.remove(id);
        temp.getSubtaskIds().forEach(subtask -> subtasks.remove(subtask));
    }

    public void removeSubtask(int id) {
        Subtask temp = subtasks.remove(id);
        if (temp == null) {
            return;
        }
        Epic epic = epics.get(temp.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    public List<Subtask> getEpicSubtasks(Epic epic) {
        ArrayList<Subtask> result = new ArrayList<>();
        for(Integer id : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(id);
            if (subtask != null) {
                result.add(subtask);
            }
        }
        return result;
    }

    private void updateEpicStatus(int epicId) {
        Epic savedEpic = epics.get(epicId);
        if(savedEpic == null) {
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