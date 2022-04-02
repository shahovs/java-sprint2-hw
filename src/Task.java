public class Task {
    private String title;
    private String description;
    private int id;

    public enum Status {NEW, DONE, IN_PROGRESS};
    private Status status;

    public Task(String title, String description, int id, Status status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String result = " Id " + id + " Title: " + title + " Description: " + description + " Status: " + status;
        return result;
    }
}
