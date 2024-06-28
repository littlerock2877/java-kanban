package ru.yandex.javacource.kozlov.schedule.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.kozlov.schedule.manager.task.TaskManager;
import ru.yandex.javacource.kozlov.schedule.task.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (pathParts.length != 2) {
            sendServerError(exchange, "Проверьте введенный вами URL и попробуйте еще раз.");
        }
        List<Task> prioritized = taskManager.getPrioritizedTasks();
        String prioritizedJsonString = gson.toJson(prioritized);
        sendText(exchange, prioritizedJsonString);
    }
}
