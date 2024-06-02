package ru.yandex.javacource.kozlov.schedule.util;

import ru.yandex.javacource.kozlov.schedule.task.*;

public class TaskConverter {
    public static String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getTaskStatus() + "," + task.getDescription() + "," + task.getEpicId();
    }

    public static Task fromString(String value) {
        final String[] fields = value.split(",");
        Task task = null;
        switch (TaskType.valueOf(fields[1])) {
            case TaskType.TASK :
                task =  new Task(Integer.parseInt(fields[0]), fields[2], fields[4], TaskStatus.valueOf(fields[3]));
                break;
            case TaskType.SUBTASK:
                task =  new Subtask(Integer.parseInt(fields[0]), fields[2], fields[4], TaskStatus.valueOf(fields[3]), Integer.parseInt(fields[5].trim()));
                break;
            case TaskType.EPIC:
                task =  new Epic(Integer.parseInt(fields[0]), fields[2], fields[4]);
                break;
        }
        return task;
    }
}