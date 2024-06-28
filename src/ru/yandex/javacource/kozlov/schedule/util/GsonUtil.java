package ru.yandex.javacource.kozlov.schedule.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.javacource.kozlov.schedule.adapter.DurationAdapter;
import ru.yandex.javacource.kozlov.schedule.adapter.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public class GsonUtil {
    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        builder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return builder.create();
    }
}
