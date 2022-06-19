package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.time.LocalDateTime;

public class SubtaskAdapter extends TypeAdapter<Subtask> {
    static final String EMPTY_STRING = "";
    static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @Override
    public void write(final JsonWriter jsonWriter, final Subtask subtask) throws IOException {
        if (subtask == null) {
            jsonWriter.value(EMPTY_STRING);
        } else {
            subtask.setEpic(null);
            String json = gson.toJson(subtask);
            System.out.println("SubtaskAdapter toJson(json):\n" + json);
            jsonWriter.value(json);
        }
    }

    @Override
    public Subtask read(final JsonReader jsonReader) throws IOException {
        String line = jsonReader.nextString();
        System.out.println("line from jsonReader: " + line);
        if (EMPTY_STRING.equals(line)) {
            return null;
        }
        Subtask subtask = gson.fromJson(line, Subtask.class);
        System.out.println("SubtaskAdapter fromJson(subtask):\n" + subtask);
        return subtask;
    }
}
