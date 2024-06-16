package ru.yandex.javacource.kozlov.schedule.util;

import ru.yandex.javacource.kozlov.schedule.task.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskConverter {
    public static String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getTaskStatus() + "," + task.getDescription() + "," + ((task.getType().equals(TaskType.SUBTASK) ? ((Subtask) task).getEpicId() : "") + "," + task.getStartTime() + "," + task.getDuration().toMinutes() + "," + task.getEndTime());
    }

    public static Task fromString(String value) {
        final String[] fields = value.split(",");
        Task task = null;
        switch (TaskType.valueOf(fields[1])) {
            case TaskType.TASK :
                task = new Task(Integer.parseInt(fields[0]), fields[2], fields[4], TaskStatus.valueOf(fields[3]), LocalDateTime.parse(fields[6]), Duration.ofMinutes(Integer.parseInt(fields[7])));
                task.setEndTime(LocalDateTime.parse(fields[8].trim()));
                break;
            case TaskType.SUBTASK:
                task = new Subtask(Integer.parseInt(fields[0]), fields[2], fields[4], TaskStatus.valueOf(fields[3]), Integer.parseInt(fields[5]), LocalDateTime.parse(fields[6]), Duration.ofMinutes(Integer.parseInt(fields[7])));
                task.setEndTime(LocalDateTime.parse(fields[8].trim()));
                break;
            case TaskType.EPIC:
                task = new Epic(Integer.parseInt(fields[0]), fields[2], fields[4]);
                task.setStartTime(LocalDateTime.parse(fields[6]));
                task.setDuration(Duration.ofMinutes(Integer.parseInt(fields[7])));
                task.setEndTime(LocalDateTime.parse(fields[8].trim()));
                break;
        }
        return task;
    }
}