package ru.yandex.javacource.kozlov.schedule.task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatus taskStatus, int epicId) {
        super(name, description, taskStatus);
        setEpicId(epicId);
    }

    public Subtask(int id, String name, String description, TaskStatus taskStatus, int epicId) {
        this(name, description, taskStatus, epicId);
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
