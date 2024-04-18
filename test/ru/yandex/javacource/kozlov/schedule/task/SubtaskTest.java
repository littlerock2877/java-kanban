package ru.yandex.javacource.kozlov.schedule.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Subtask")
public class SubtaskTest {
    @Test
    @DisplayName("Should equals with same id")
    public void shouldEqualsWithSameId() {
        Subtask subtask1 = new Subtask("subtask1", "subtask1 description", TaskStatus.NEW, 0);
        Subtask subtask2 = new Subtask("subtask2", "subtask2 description", TaskStatus.DONE, 2);
        Assertions.assertEquals(subtask1.getId(), subtask2.getId(), "subtask1.getId() != subtask2.getId()");
        Assertions.assertEquals(subtask1, subtask2, "subtask1 != subtask2");
    }
}
