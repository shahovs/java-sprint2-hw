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
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public void updateSubtasks(ArrayList<Subtask> updatedSubtasks) {
        subtasks = updatedSubtasks;
    }

    @Override
    public TypesOfTasks getType() {
        return TypesOfTasks.EPIC;
    }

    public void setTimesAndDuration() {
        calculateAndSetStartTime();
        calculateAndSetDuration();
        calculateAndSetFinishTime();
    }

    private void calculateAndSetStartTime() {
        if (subtasks.size() == 0 || subtasks == null) {
            setStartTime(null);
            return;
        }

        LocalDateTime earliestTime = subtasks.get(0).getStartTime();

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime() != null && earliestTime.isAfter(subtask.getStartTime())) {
                earliestTime = subtask.getStartTime();
            }
        }
        setStartTime(earliestTime);
    }

    private void calculateAndSetFinishTime() {
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

    private void calculateAndSetDuration() {
        int count = 0;
        for (Subtask subtask : subtasks) {
            count += subtask.getDuration();
        }
        setDuration(count);
    }

//
//    @Override
//    public void setDuration(int duration) {
//        this.duration = duration;
//        setFinishTime();
//    }
//
//    @Override
//    void setFinishTime() {
//
//    }

}
