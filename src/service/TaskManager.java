package service;

import model.Epic;
import model.Subtask;
import model.Task;
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
        Epic targetEpic = subtask.getEpic();
        subtask.setId(id);
        targetEpic.addSubtask(subtask);
        subtasks.put(id, subtask);
        targetEpic.updateStatus();
        return subtask;
    }

    public List<Task> getAllTasks() {
        return tasks.values().stream().toList();
    }

    public List<Epic> getAllEpics() {
        return epics.values().stream().toList();
    }

    public List<Subtask> getAllSubtasks() {
        return subtasks.values().stream().toList();
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
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        Subtask currentSubtask = subtasks.get(subtask.getId());
        currentSubtask.setName(subtask.getName());
        currentSubtask.setDescription(subtask.getDescription());
        currentSubtask.setTaskStatus(subtask.getTaskStatus());
        Epic targetEpic = subtask.getEpic();
        if (!currentSubtask.getEpic().equals(targetEpic)) {
            currentSubtask.getEpic().removeSubtask(currentSubtask);
            currentSubtask.setEpic(targetEpic);
            targetEpic.addSubtask(currentSubtask);
        }
        subtask.getEpic().updateStatus();
    }

    public void updateEpic(Epic epic) {
        Epic currentEpic = epics.get(epic.getId());
        currentEpic.setName(epic.getName());
        currentEpic.setDescription(epic.getDescription());
        currentEpic.updateStatus();
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.entrySet().stream().forEach(epic -> removeEpic(epic.getKey()));
    }

    public void removeAllSubtasks() {
        subtasks.entrySet().stream().forEach(subtask -> removeSubtask(subtask.getKey()));
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeEpic(int id) {
        Epic temp = epics.remove(id);
        subtasks.entrySet().removeIf(subtask -> subtask.getValue().getEpic().equals(temp));
    }

    public void removeSubtask(int id) {
        Subtask temp = subtasks.remove(id);
        temp.getEpic().removeSubtask(temp);
    }

    public List<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtasks();
    }

    private int generateId() {
        return ++counter;
    }
}