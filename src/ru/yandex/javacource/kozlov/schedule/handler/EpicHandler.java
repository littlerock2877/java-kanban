package ru.yandex.javacource.kozlov.schedule.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.kozlov.schedule.exception.NotFoundException;
import ru.yandex.javacource.kozlov.schedule.exception.ValidationException;
import ru.yandex.javacource.kozlov.schedule.manager.task.TaskManager;
import ru.yandex.javacource.kozlov.schedule.task.Epic;
import ru.yandex.javacource.kozlov.schedule.task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET_ALL: {
                handleGetEpics(exchange);
                break;
            }
            case GET : {
                handleGetSpecifiedEpic(exchange);
                break;
            }
            case POST: {
                handleCreateEpic(exchange);
                break;
            }
            case DELETE: {
                handleDeleteEpic(exchange);
                break;
            }
            case GET_SUBTASKS: {
                handleGetSubtasks(exchange);
                break;
            }
            default: sendServerError(exchange, "Не удалось найти выбранный эндпоинт");
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = getEpicId(exchange);
        if (epicIdOpt.isEmpty()) {
            sendServerError(exchange, "Проверьте введенный вами URL и попробуйте еще раз.");
            return;
        }
        int epicId = epicIdOpt.get();
        try {
            Epic epic = taskManager.getEpic(epicId);
            List<Subtask> subtasks = taskManager.getEpicSubtasks(epic);
            String subtasksJsonString = gson.toJson(subtasks);
            sendText(exchange, subtasksJsonString);
        } catch (NotFoundException e) {
            sendNotFound(exchange, String.format("Эпик с ID %d не найден", epicId));
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = getEpicId(exchange);
        if (epicIdOpt.isEmpty()) {
            sendServerError(exchange, "Проверьте введенный вами URL и попробуйте еще раз.");
            return;
        }
        int epicId = epicIdOpt.get();
        taskManager.removeEpic(epicId);
        sendText(exchange, String.format("Эпик с ID %d был успешно удален", epicId));
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        String epicJsonString = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(epicJsonString, Epic.class);
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 2) {
            Epic createdEpic = taskManager.createEpic(epic);
            sendCreated(exchange, String.format("Эпик с ID %d был успешно создан", createdEpic.getId()));
            return;
        }
        Optional<Integer> epicIdOpt = getEpicId(exchange);
        if (epicIdOpt.isEmpty()) {
            sendServerError(exchange, "Проверьте введенный вами URL и попробуйте еще раз.");
            return;
        }
        int epicId = epicIdOpt.get();
        epic.setId(epicId);
        try {
            taskManager.updateEpic(epic);
            sendText(exchange, String.format("Эпик с ID %d был успешно обновлен", epicId));
        } catch (NotFoundException e) {
            sendNotFound(exchange, String.format("Эпик с ID %d не найден", epicId));
        } catch (ValidationException e) {
            sendHasInteractions(exchange, String.format("Эпик с ID %d пересекается с другой задачей", epicId));
        }
    }

    private void handleGetSpecifiedEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = getEpicId(exchange);
        if (epicIdOpt.isEmpty()) {
            sendServerError(exchange, "Проверьте введенный вами URL и попробуйте еще раз.");
            return;
        }
        int epicId = epicIdOpt.get();
        try {
            Epic epic = taskManager.getEpic(epicId);
            String epicJsonString = gson.toJson(epic);
            sendText(exchange, epicJsonString);
        } catch (NotFoundException e) {
            sendNotFound(exchange, String.format("Эпик с ID %d не найден", epicId));
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        List<Epic> tasks =  taskManager.getAllEpics();
        String epicsJsonString = gson.toJson(tasks);
        sendText(exchange, epicsJsonString);
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
        } else if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")) {
            return Endpoint.GET_SUBTASKS;
        }
        return Endpoint.UNKNOWN;
    }

    private Optional<Integer> getEpicId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
