/**
 *
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Пришло время практики!");
        Manager manager = new Manager();

        Task task1 = new Task("Shopping", "Coat, T-short, Cap", 0, Task.Status.NEW);
        Task task2 = new Task("Buy food", "apples, bread, milk", 0, Task.Status.NEW);

        Epic epic1 = new Epic("Rest", "Eat, watch YouTube, sleep", 0);
        Subtask subtask1 = new Subtask("Eat", "Taste something new", 0, Task.Status.NEW, epic1);
        Subtask subtask2 = new Subtask("Watch YouTube", "Find something interesting",
                0, Task.Status.NEW, epic1);
        Epic epic2 = new Epic("Become programmer", "And find a job", 0);
        Subtask subtask3 = new Subtask("Learn Java", "practise, read lessons",
                0, Task.Status.NEW, epic2);

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createEpic(epic2);
        manager.createSubtask(subtask3);
        System.out.println(manager);

        Task task3 = new Task("Cooking", "Soap and spaghetti", 1, Task.Status.IN_PROGRESS);
        Task task4 = new Task("Cleaning", "Kitchen and room", 2, Task.Status.DONE);
        manager.updateTask(task3);
        manager.updateTask(task4);
        Epic epic3 = new Epic("Write a story", "For me and friends", 3);
        manager.updateEpic(epic3);
        Subtask subtask4 = new Subtask("Create a plot", "interesting, unusual, with funny end", 4,
                Task.Status.DONE, epic3);
        manager.updateSubtask(subtask4);
        subtask3.setStatus(Task.Status.DONE);
        manager.updateSubtask(subtask3);
        System.out.println(manager);

        task3.setStatus(Task.Status.DONE);
        manager.updateTask(task3);
        manager.removeTask(1);
        manager.removeEpic(6);
        System.out.println(manager);

    }
}
