package ru.yandex.javacource.kozlov.schedule.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.kozlov.schedule.exception.NotFoundException;
import ru.yandex.javacource.kozlov.schedule.exception.ValidationException;
import ru.yandex.javacource.kozlov.schedule.manager.task.TaskManager;
import ru.yandex.javacource.kozlov.schedule.task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET_ALL: {
                handleGetSubtasks(exchange);
                break;
            }
            case GET : {
                handleGetSpecifiedSubtask(exchange);
                break;
            }
            case POST: {
                handleCreateSubtask(exchange);
                break;
            }
            case DELETE: {
                handleDeleteSubtask(exchange);
                break;
            }
            default: sendServerError(exchange, "Не удалось найти выбранный эндпоинт");
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> subtaskIdOpt = getSubtaskId(exchange);
        if (subtaskIdOpt.isEmpty()) {
            sendServerError(exchange, "Проверьте введенный вами URL и попробуйте еще раз.");
            return;
        }
        int subtaskId = subtaskIdOpt.get();
        taskManager.removeSubtask(subtaskId);
        sendText(exchange, String.format("Подзадача с ID %d была успешно удалена", subtaskId));
    }

    private void handleCreateSubtask(HttpExchange exchange) throws IOException {
        String subtaskJsonString = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(subtaskJsonString, Subtask.class);
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 2) {
            Subtask createdSubtask = taskManager.createSubtask(subtask);
            sendCreated(exchange, String.format("Подзадача с ID %d была успешно создана", createdSubtask.getId()));
            return;
        }
        Optional<Integer> subtaskIdOpt = getSubtaskId(exchange);
        if (subtaskIdOpt.isEmpty()) {
            sendServerError(exchange, "Проверьте введенный вами URL и попробуйте еще раз.");
            return;
        }
        int subtaskId = subtaskIdOpt.get();
        subtask.setId(subtaskId);
        try {
            taskManager.updateSubtask(subtask);
            sendText(exchange, String.format("Подзадача с ID %d была успешно обновлена", subtaskId));
        } catch (NotFoundException e) {
            sendNotFound(exchange, String.format("Подзадача с ID %d не найдена", subtaskId));
        } catch (ValidationException e) {
            sendHasInteractions(exchange, String.format("Подзадача с ID %d пересекается с другой задачей", subtaskId));
        }
    }

    private void handleGetSpecifiedSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getSubtaskId(exchange);
        if (taskIdOpt.isEmpty()) {
            sendServerError(exchange, "Проверьте введенный вами URL и попробуйте еще раз.");
            return;
        }
        int subtaskId = taskIdOpt.get();
        try {
            Subtask task = taskManager.getSubtask(subtaskId);
            String subtaskJsonString = gson.toJson(task);
            sendText(exchange, subtaskJsonString);
        } catch (NotFoundException e) {
            sendNotFound(exchange, String.format("Подзадача с ID %d не найдена", subtaskId));
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> tasks =  taskManager.getAllSubtasks();
        String subtasksJsonString = gson.toJson(tasks);
        sendText(exchange, subtasksJsonString);
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

    private Optional<Integer> getSubtaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
