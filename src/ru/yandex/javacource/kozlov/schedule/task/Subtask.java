package ru.yandex.javacource.kozlov.schedule.task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatus taskStatus, int epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, taskStatus, startTime, duration);
        setEpicId(epicId);
    }

    public Subtask(int id, String name, String description, TaskStatus taskStatus, int epicId, LocalDateTime startTime, Duration duration) {
        this(name, description, taskStatus, epicId, startTime, duration);
        setId(id);
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
