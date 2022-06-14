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
        setStartTimeDurationAndCalculateFinish(null, 0);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        startTime = null;
        duration = 0;
        finishTime = null;
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        setStartTimeDurationAndCalculateFinish(null, 0);
    }

    public void updateSubtasks(ArrayList<Subtask> updatedSubtasks) {
        subtasks = updatedSubtasks;
    }

    @Override
    public TypesOfTasks getType() {
        return TypesOfTasks.EPIC;
    }

    @Override
    void setStartTimeOnly(LocalDateTime startTime) { // Аргумент не используется
        if (subtasks == null || subtasks.size() == 0) {
            this.startTime = null;
            return;
        }

        LocalDateTime earliestTime = subtasks.get(0).getStartTime();
        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime() != null && earliestTime.isAfter(subtask.getStartTime())) {
                earliestTime = subtask.getStartTime();
            }
        }
        this.startTime = earliestTime;
    }

    @Override
    void setDurationOnly(int duration) {  // Аргумент не используется
        int count = 0;
        for (Subtask subtask : subtasks) {
            count += subtask.getDuration();
        }
        this.duration = count;
    }

    @Override
    void calculateFinishTime() {
        if (subtasks == null || subtasks.size() == 0) {
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
