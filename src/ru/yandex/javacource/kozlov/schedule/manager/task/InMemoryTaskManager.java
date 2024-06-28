package ru.yandex.javacource.kozlov.schedule.manager.task;

import ru.yandex.javacource.kozlov.schedule.exception.NotFoundException;
import ru.yandex.javacource.kozlov.schedule.exception.ValidationException;
import ru.yandex.javacource.kozlov.schedule.manager.history.HistoryManager;
import ru.yandex.javacource.kozlov.schedule.task.Epic;
import ru.yandex.javacource.kozlov.schedule.task.Subtask;
import ru.yandex.javacource.kozlov.schedule.task.Task;
import ru.yandex.javacource.kozlov.schedule.task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int counter;
    private final HistoryManager historyManager;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Subtask> subtasks;
    protected final Map<Integer, Epic> epics;

    TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
        checkTaskTime(task);
        prioritizedTasks.remove(task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        updateEpic(id);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        int id = generateId();
        Epic targetEpic = epics.get(subtask.getEpicId());
        if (targetEpic == null) {
            throw new NotFoundException(String.format("Эпик с id %d не найден", subtask.getEpicId()));
        }
        subtask.setId(id);
        targetEpic.addSubtask(subtask.getId());
        subtasks.put(id, subtask);
        checkTaskTime(subtask);
        prioritizedTasks.remove(subtask);
        prioritizedTasks.add(subtask);
        updateEpic(targetEpic.getId());
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
        if (task == null) {
            throw new NotFoundException(String.format("Задача с id %d не найдена", id));
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException(String.format("Эпик с id %d не найден", id));
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException(String.format("Подзадача с id %d не найдена", id));
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        Task savedTask = tasks.get(id);
        if (savedTask == null) {
            throw new NotFoundException(String.format("Задача с id %d не найдена", id));
        }
        checkTaskTime(task);
        prioritizedTasks.remove(savedTask);
        prioritizedTasks.add(task);
        tasks.put(id, task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();
        Subtask savedSubtask = subtasks.get(subtaskId);
        if (savedSubtask == null) {
            throw new NotFoundException(String.format("Подзадача с id %d не найдена", subtaskId));
        }
        checkTaskTime(subtask);
        prioritizedTasks.remove(savedSubtask);
        prioritizedTasks.add(subtask);
        int targetEpicId = subtask.getEpicId();
        Epic targetEpic = epics.get(targetEpicId);
        if (targetEpic == null) {
            throw new NotFoundException(String.format("Эпик с id %d не найден", targetEpicId));
        }
        subtasks.put(subtaskId, subtask);
        updateEpic(targetEpic.getId());
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();
        Epic savedEpic = epics.get(id);
        if (savedEpic == null) {
            throw new NotFoundException(String.format("Эпик с id %d не найден", id));
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        updateEpic(savedEpic.getId());
        epics.put(id, savedEpic);
    }

    @Override
    public void removeAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();
        epics.keySet().forEach(historyManager::remove);
        epics.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            updateEpic(epic.getId());
        }
        subtasks.keySet().forEach(historyManager::remove);
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
            throw new NotFoundException(String.format("Подзадача с id %d не найдена", id));
        }
        Epic epic = epics.get(temp.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        return subtasks.entrySet().stream()
                .filter(subtask -> epic.getSubtaskIds().contains(subtask.getKey()))
                .filter(subtask -> subtask.getValue() != null)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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

    private void updateEpicTime(int epicId) {
        Epic savedEpic = epics.get(epicId);
        if (savedEpic == null) {
            return;
        }
        if (savedEpic.getSubtaskIds().isEmpty()) {
            savedEpic.setStartTime(LocalDateTime.now());
            savedEpic.setDuration(Duration.ZERO);
            savedEpic.setEndTime(LocalDateTime.now());
            return;
        }
        LocalDateTime startTime = LocalDateTime.MAX;
        LocalDateTime endTime = LocalDateTime.MIN;
        Duration duration = Duration.ZERO;
        for (Integer subtaskId : savedEpic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) {
                continue;
            }
            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
            duration = duration.plus(subtask.getDuration());
        }
        savedEpic.setStartTime(startTime);
        savedEpic.setEndTime(endTime);
        savedEpic.setDuration(duration);
    }


        private int generateId() {
        return ++counter;
    }

    private void checkTaskTime(Task task) throws ValidationException{
        for (Task prioritized : prioritizedTasks) {
            if (prioritized.getId() == task.getId()) {
                continue;
            }
            if (task.getStartTime().isBefore(prioritized.getEndTime()) && prioritized.getStartTime().isBefore(task.getEndTime())) {
                throw new ValidationException(String.format("Задача %s пересекается с уже существующей задачей %s", task.getName(), prioritized.getName()));
            }
        }
    }

    private void updateEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        updateEpicTime(epicId);
        updateEpicStatus(epicId);
    }
}