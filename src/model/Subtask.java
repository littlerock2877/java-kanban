package model;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description, TaskStatus taskStatus, Epic epic) {
        super(name, description, taskStatus);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
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
