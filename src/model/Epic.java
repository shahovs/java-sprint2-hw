package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description, int id) {
        super(name, description, id, Task.Status.NEW);
        subtasks = new ArrayList<>();
    }

    public Epic(String name, String description) {
        this(name, description, 0);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        setStartTimeAndDuration();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        startTime = null;
        duration = 0;
        finishTime = null;
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        setStartTimeAndDuration();
    }

    public void updateSubtasks(ArrayList<Subtask> updatedSubtasks) {
        subtasks = updatedSubtasks;
    }

    @Override
    public TypesOfTasks getType() {
        return TypesOfTasks.EPIC;
    }

    public void setStartTimeAndDuration() {
        setStartTime(startTime);
        setDuration(duration);
    }

    /* Привет Ульяна!
    * Спасибо!
    * Долго прикидывал разные варианты, как лучше организовать методы.
    * В итоге получилось то, что есть сейчас.
    * Больше всего не хотелось убирать модификаторы private из полей класса-родителя (task).
    * Но по-другому не придумал. Да и наставник в своем коде тоже давал пакетный доступ к полям.
    * Хорошего дня!
    * */

    @Override
    public void setStartTime(LocalDateTime st) {
        if (subtasks.size() == 0 || subtasks == null) {
            startTime = null;
            return;
        }

        LocalDateTime earliestTime = subtasks.get(0).getStartTime();
        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime() != null && earliestTime.isAfter(subtask.getStartTime())) {
                earliestTime = subtask.getStartTime();
            }
        }
        startTime = earliestTime;
        calculateFinishTime();
    }

    @Override
    public void setDuration(int d) {
        int count = 0;
        for (Subtask subtask : subtasks) {
            count += subtask.getDuration();
        }
        duration = count;
        calculateFinishTime();
    }

    private void calculateFinishTime() {
        if (subtasks.size() == 0 || subtasks == null) {
            finishTime = null;
            return;
        }
        LocalDateTime latestTime = subtasks.get(0).getFinishTime();
        for (Subtask subtask : subtasks) {
            if (subtask.getFinishTime() != null && latestTime.isBefore(subtask.getFinishTime())) {
                latestTime = subtask.getFinishTime();
            }
        }
        finishTime = latestTime;
    }

    public void setStatusOfEpic() {
        if (subtasks == null || subtasks.size() == 0) {
            status = Task.Status.NEW;
            return;
        }
        Task.Status st = subtasks.get(0).getStatus();
        status = st;
        for (Subtask subtask : subtasks) {
            if (status != subtask.getStatus()) {
                status = Task.Status.IN_PROGRESS;
                return;
            }
        }
    }

}
