package ru.yandex.javacource.kozlov.schedule.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

@DisplayName("Task")
public class TaskTest {
    @Test
    @DisplayName("Should equals with same id")
    public void shouldEqualsWithSameId() {
        Task task1 = new Task("task1", "task1 description", TaskStatus.NEW, LocalDateTime.now().minusDays(11), Duration.ofSeconds(1));
        Task task2 = new Task("task2", "task2 description", TaskStatus.DONE, LocalDateTime.now().minusDays(10), Duration.ofMillis(1000));
        Assertions.assertEquals(task1.getId(), task2.getId(), "task1.getId() != task2.getId()");
        Assertions.assertEquals(task1, task2, "task1 != task2");
    }
}
