package exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(Exception e) {
        super(e);
    }
}
