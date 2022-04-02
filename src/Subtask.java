public class Subtask extends Task {
    private Epic epic;

    public Subtask(String title, String description, int id, Status status, Epic epic) {
        super(title, description, id, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

}
