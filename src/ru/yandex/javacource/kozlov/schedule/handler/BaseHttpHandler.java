package ru.yandex.javacource.kozlov.schedule.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.kozlov.schedule.manager.task.TaskManager;
import ru.yandex.javacource.kozlov.schedule.util.GsonUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected TaskManager taskManager;
    protected Gson gson = GsonUtil.getGson();

    protected void sendText(HttpExchange h, String text) throws IOException {
        send(h, text, 200);
    }

    protected void sendCreated(HttpExchange h, String text) throws IOException {
        send(h, text, 201);
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        send(h, text, 404);
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        send(h, text, 406);
    }

    protected void sendServerError(HttpExchange h, String text) throws IOException {
        send(h, text, 500);
    }

    private void send(HttpExchange h, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(statusCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }
}
