package ru.yandex.javacource.kozlov.schedule.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Epic")
public class EpicTest {
    @Test
    @DisplayName("Should equals with same id")
    public void shouldEqualsWithSameId() {
        Epic epic1 = new Epic("epic1", "epic1 description");
        Epic epic2 = new Epic("epic2", "epic2 description");
        Assertions.assertEquals(epic1.getId(), epic2.getId(), "epic1.getId() != epic2.getId()");
        Assertions.assertEquals(epic1, epic2, "epic1 != epic2");
    }

    @Test
    @DisplayName("Should add new subtask")
    public void shouldAddNewSubtask() {
        Epic epic = new Epic("epic", "description");
        Subtask subtask = new Subtask("subtask", "description",TaskStatus.NEW, epic.getId());
        epic.addSubtask(subtask.getId());
        Assertions.assertTrue(epic.getSubtaskIds().contains(subtask.getEpicId()), "!epic.getSubtaskIds().contains(subtask.getEpicId()");
    }

    @Test
    @DisplayName("Should remove exist subtask")
    public void shouldRemoveExistSubtask() {
        Epic epic = new Epic("epic", "description");
        Subtask subtask = new Subtask("subtask", "description",TaskStatus.NEW, epic.getId());
        epic.addSubtask(subtask.getId());
        epic.removeSubtask(subtask.getId());
        Assertions.assertTrue(!epic.getSubtaskIds().contains(subtask.getEpicId()), "epic.getSubtaskIds().contains(subtask.getEpicId()");
    }

    @Test
    @DisplayName("Should remove all subtasks")
    public void shouldRemoveAllSubtasks() {
        Epic epic = new Epic("epic", "description");
        Subtask subtask1 = new Subtask("subtask1", "description",TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("subtask2", "description",TaskStatus.DONE, epic.getId());
        epic.addSubtask(subtask1.getId());
        epic.addSubtask(subtask2.getId());
        epic.removeAllSubtasks();
        Assertions.assertTrue(epic.getSubtaskIds().size() == 0, "epic.getSubtaskIds().size() != 0");
    }
}
