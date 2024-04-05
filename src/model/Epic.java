package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    @Override
    public void setTaskStatus(TaskStatus taskStatus) {
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            this.taskStatus = TaskStatus.NEW;
            return;
        }
        var firstSubtaskStatus = subtasks.getFirst().taskStatus;
        if (subtasks.stream().allMatch(subtask -> subtask.getTaskStatus() == firstSubtaskStatus)) {
            this.taskStatus = firstSubtaskStatus;
            return;
        }
        this.taskStatus = TaskStatus.IN_PROGRESS;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", taskStatus=" + taskStatus +
                ", subtasks=" + getSubtasks() +
                '}';
    }
}
