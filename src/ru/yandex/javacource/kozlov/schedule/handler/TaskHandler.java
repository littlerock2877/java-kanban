package ru.yandex.javacource.kozlov.schedule.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.kozlov.schedule.exception.NotFoundException;
import ru.yandex.javacource.kozlov.schedule.exception.ValidationException;
import ru.yandex.javacource.kozlov.schedule.manager.task.TaskManager;
import ru.yandex.javacource.kozlov.schedule.task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET_ALL: {
                handleGetTasks(exchange);
                break;
            }
            case GET : {
                handleGetSpecifiedTask(exchange);
                break;
            }
            case POST: {
                handleCreateTask(exchange);
                break;
            }
            case DELETE: {
                handleDeleteTask(exchange);
                break;
            }
            default: sendServerError(exchange, "Не удалось найти выбранный эндпоинт");
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(exchange);
        if (taskIdOpt.isEmpty()) {
            sendServerError(exchange, "Проверьте введенный вами URL и попробуйте еще раз.");
            return;
        }
        int taskId = taskIdOpt.get();
        taskManager.removeTask(taskId);
        sendText(exchange, String.format("Задача с ID %d была успешно удалена", taskId));
    }

    private void handleCreateTask(HttpExchange exchange) throws IOException {
        String taskJsonString = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(taskJsonString, Task.class);
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 2) {
            Task createdTask = taskManager.createTask(task);
            sendCreated(exchange, String.format("Задача с ID %d была успешно создана", createdTask.getId()));
            return;
        }
        Optional<Integer> taskIdOpt = getTaskId(exchange);
        if (taskIdOpt.isEmpty()) {
            sendServerError(exchange, "Проверьте введенный вами URL и попробуйте еще раз.");
            return;
        }
        int taskId = taskIdOpt.get();
        task.setId(taskId);
        try {
            taskManager.updateTask(task);
            sendText(exchange, String.format("Задача с ID %d была успешно обновлена", taskId));
        } catch (NotFoundException e) {
            sendNotFound(exchange, String.format("Задача с ID %d не найдена", taskId));
        } catch (ValidationException e) {
            sendHasInteractions(exchange, String.format("Задача с ID %d пересекается с другой задачей", taskId));
        }
    }

    private void handleGetSpecifiedTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(exchange);
        if (taskIdOpt.isEmpty()) {
            sendServerError(exchange, "Проверьте введенный вами URL и попробуйте еще раз.");
            return;
        }
        int taskId = taskIdOpt.get();
        try {
            Task task = taskManager.getTask(taskId);
            String taskJsonString = gson.toJson(task);
            sendText(exchange, taskJsonString);
        } catch (NotFoundException e) {
            sendNotFound(exchange, String.format("Задача с ID %d не найдена", taskId));
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks =  taskManager.getAllTasks();
        String tasksJsonString = gson.toJson(tasks);
        sendText(exchange, tasksJsonString);
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_ALL;
            }
            return Endpoint.POST;
        } else if (pathParts.length == 3) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET;
            } else if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE;
            }
            return Endpoint.POST;
        }
        return Endpoint.UNKNOWN;
    }

    private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
