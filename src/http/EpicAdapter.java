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

public class EpicAdapter extends TypeAdapter<Epic> {
    static final String EMPTY_STRING = "";
    static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @Override
    public void write(final JsonWriter jsonWriter, final Epic epic) throws IOException {
        if (epic == null) {
            jsonWriter.value(EMPTY_STRING);
        } else {
            Task task = new Task(epic.getName(), epic.getDescription(), epic.getId(), epic.getStatus(),
                    epic.getStartTime(), epic.getDuration());
//            String jsonTask = gson.toJson(task);
//            int subtasksCount = epic.getSubtasks().size();
//            int[] subtasksId = new int[epic.getSubtasks().size()];
//            int i = 0;
//            for (Subtask subtask : epic.getSubtasks()) {
//                subtasksId[i] = subtask.getId();
//                i++;
//            }
//            String jsonEpic = jsonTask + epic.getFinishTime() + subtasksId;
            // или второй и 3-ий элемент тоже gson.toJson ?

            String json = gson.toJson(task);
            System.out.println("EpicAdapter json(task):\n" + json);
            jsonWriter.jsonValue(json);
//            jsonWriter.value(json);
        }
    }

    @Override
    public Epic read(final JsonReader jsonReader) throws IOException {
        String line = jsonReader.nextString();
        if (EMPTY_STRING.equals(line)) {
            return null;
        }
        Task task = gson.fromJson(line, Task.class);
        Epic epic = new Epic(task.getName(), task.getDescription(), task.getId());
        return epic;
    }

}
