package ru.yandex.javacource.kozlov.schedule.manager.task;

import ru.yandex.javacource.kozlov.schedule.exception.ManagerSaveException;
import ru.yandex.javacource.kozlov.schedule.manager.Managers;
import ru.yandex.javacource.kozlov.schedule.manager.history.HistoryManager;
import ru.yandex.javacource.kozlov.schedule.task.Epic;
import ru.yandex.javacource.kozlov.schedule.task.Subtask;
import ru.yandex.javacource.kozlov.schedule.task.Task;
import ru.yandex.javacource.kozlov.schedule.task.TaskStatus;
import ru.yandex.javacource.kozlov.schedule.util.TaskConverter;
import java.io.*;
import java.nio.file.Files;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private static final File DEFAULT_FILE = new File("resources/task.csv");

    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public FileBackedTaskManager(HistoryManager historyManager) {
        this(historyManager, DEFAULT_FILE);
    }

    public FileBackedTaskManager(File file) {
        this(Managers.getDefaultHistory(), file);
    }

    public static FileBackedTaskManager restoreFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        fileBackedTaskManager.loadFromFile();
        return fileBackedTaskManager;
    }

    public static void main(String[] args) {
        FileBackedTaskManager manager = new FileBackedTaskManager(DEFAULT_FILE);

        System.out.println("MANAGER");

        Task task1 = new Task("first task", "first task description", TaskStatus.NEW);
        Task task2 = new Task("second task", "second task description", TaskStatus.DONE);

        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("first epic", "first epic description");
        Epic epic2 = new Epic("second epic", "second epic description");

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        Subtask subtask1 = new Subtask("first subtask", "first subtask description", TaskStatus.IN_PROGRESS, epic2.getId());
        Subtask subtask2 = new Subtask("second subtask", "second subtask description", TaskStatus.DONE, epic2.getId());

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.tasks.forEach((key, value) -> System.out.println(value));
        manager.epics.forEach((key, value) -> System.out.println(value));
        manager.subtasks.forEach((key, value) -> System.out.println(value));

        System.out.println();
        System.out.println("RESTORED MANAGER");

        FileBackedTaskManager restoredManager = FileBackedTaskManager.restoreFromFile(DEFAULT_FILE);

        restoredManager.tasks.forEach((key, value) -> System.out.println(value));
        restoredManager.epics.forEach((key, value) -> System.out.println(value));
        restoredManager.subtasks.forEach((key, value) -> System.out.println(value));

        System.out.println();
        System.out.println(manager.tasks.size() == restoredManager.tasks.size());
        System.out.println(manager.epics.size() == restoredManager.epics.size());
        System.out.println(manager.subtasks.size() == restoredManager.subtasks.size());
    }

    @Override
    public Task createTask(Task task) {
        Task result =  super.createTask(task);
        save();
        return result;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic result = super.createEpic(epic);
        save();
        return result;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask result = super.createSubtask(subtask);
        save();
        return result;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append("id,type,name,status,description,epic");
            writer.newLine();
            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.append(TaskConverter.toString(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.append(TaskConverter.toString(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                writer.append(TaskConverter.toString(entry.getValue()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Failed to write to file: " + file.getAbsolutePath() + "." + e.getMessage());
        }
    }

    private void loadFromFile() {
        int maxId = 0;

        try {
            String fileContent = Files.readString(file.toPath());
            String[] taskStrings = fileContent.split("\n");
            for (int i = 1; i < taskStrings.length; i++) {
                Task task = TaskConverter.fromString(taskStrings[i]);
                final int id = task.getId();
                if (id > maxId) {
                    maxId = id;
                }
                switch (task.getType()) {
                    case TASK:
                        tasks.put(id, task);
                        break;
                    case SUBTASK:
                        subtasks.put(id, (Subtask) task);
                        break;
                    case EPIC:
                        epics.put(id, (Epic) task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Failed to read from file: " + file.getAbsolutePath() + "." + e.getMessage());
        }
        counter = maxId;
    }
}
