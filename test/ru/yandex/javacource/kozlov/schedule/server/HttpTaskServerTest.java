package ru.yandex.javacource.kozlov.schedule.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import ru.yandex.javacource.kozlov.schedule.manager.Managers;
import ru.yandex.javacource.kozlov.schedule.manager.task.InMemoryTaskManager;
import ru.yandex.javacource.kozlov.schedule.manager.task.TaskManager;
import ru.yandex.javacource.kozlov.schedule.task.Task;
import ru.yandex.javacource.kozlov.schedule.task.TaskStatus;
import ru.yandex.javacource.kozlov.schedule.util.GsonUtil;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HttpTaskServerTest")
public class HttpTaskServerTest {
    TaskManager manager = new InMemoryTaskManager(Managers.getDefaultHistory());
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = GsonUtil.getGson();

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    @DisplayName("test task")
    public void testTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", TaskStatus.NEW, LocalDateTime.now().minusDays(15), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest createRequest = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode());
        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");

        HttpRequest getAllRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> getAllResponse = client.send(getAllRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getAllResponse.statusCode());
        List<Task> gotTasks = gson.fromJson(getAllResponse.body(), new TypeToken<List<Task>>(){}.getType());
        assertEquals(1, gotTasks.size(), "Некорректное количество задач");
        assertEquals("Test 2", gotTasks.getFirst().getName(), "Некорректное имя задачи");

        URI longUrl = URI.create(String.format("http://localhost:8080/tasks/%d", gotTasks.getFirst().getId()));
        task.setName("New name");
        taskJson = gson.toJson(task);
        HttpRequest updateRequest = HttpRequest.newBuilder().uri(longUrl).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, updateResponse.statusCode());
        assertEquals("New name", manager.getAllTasks().getFirst().getName(), "Некорректное имя задачи");

        HttpRequest getRequest = HttpRequest.newBuilder().uri(longUrl).GET().build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());
        Task gotTask = gson.fromJson(getResponse.body(), Task.class);
        assertEquals("New name", gotTask.getName(), "Некорректное имя задачи");

        HttpRequest deleteRequest = HttpRequest.newBuilder().uri(longUrl).DELETE().build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode());
        assertEquals(0, manager.getAllTasks().size(), "Некорректное колличество задач");
    }

    @Test
    @DisplayName("testHistory")
    public void testHistory() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", TaskStatus.NEW, LocalDateTime.now().minusDays(15), Duration.ofMinutes(5));
        manager.createTask(task);
        int taskId = manager.getAllTasks().getFirst().getId();
        manager.getTask(taskId);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");

        HttpRequest getHistoryRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> getHistoryResponse = client.send(getHistoryRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getHistoryResponse.statusCode());
        List<Task> gotHistory = gson.fromJson(getHistoryResponse.body(), new TypeToken<List<Task>>(){}.getType());
        assertEquals(1, gotHistory.size(), "Некорректное количество задач в истории");
        assertEquals("Test 2", gotHistory.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("testPrioritized")
    public void testPrioritized() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", TaskStatus.NEW, LocalDateTime.now().minusDays(15), Duration.ofMinutes(5));
        manager.createTask(task);
        int taskId = manager.getAllTasks().getFirst().getId();
        manager.getTask(taskId);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");

        HttpRequest getPrioritizedRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> getPrioritizedResponse = client.send(getPrioritizedRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getPrioritizedResponse.statusCode());
        List<Task> gotPrioritized = gson.fromJson(getPrioritizedResponse.body(), new TypeToken<List<Task>>(){}.getType());
        assertEquals(1, gotPrioritized.size(), "Некорректное количество задач в списке приоритета");
        assertEquals("Test 2", gotPrioritized.getFirst().getName(), "Некорректное имя задачи");
    }
}
