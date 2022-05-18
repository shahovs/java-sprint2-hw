// Все тесты перенесены в метод main() класса FileBackedTasksManager (как и предлагают в задании)


//import manager.FileBackedTasksManager;
//import manager.TaskManager;
//import manager.Managers;
//import model.Epic;
//import model.Subtask;
//import model.Task;
//
//import java.io.File;
//
///**
// *
// */
public class Main {
    public static void main(String[] args) {
    }
}

//        System.out.println("Пришло время практики!");
//        TaskManager manager = Managers.getDefault(); // Менеджер без загрузки начальных данных.
//
//        Task task11 = new Task("(taskName11)", "(description)");
////        Epic epic1 = new Epic("(epicName1)", "(description)");
////        Subtask subtask1 = new Subtask("(subtaskName1)", "(description)", epic1);
//        Task task12 = new Task("(taskName12)", "(description)");
//
//        manager.createTask(task11);
////        manager.createEpic(epic1);
////        manager.createSubtask(subtask1);
////
////        manager.getTask(1);
////        manager.getEpic(2);
////        manager.getSubtask(3);
//        manager.createTask(task12);
//
//
//
//        TaskManager managerFromFile = new FileBackedTasksManager(new File("tasks.csv"), true);
//        // Менеджер с загрузкой начальных данных из файла
//
//        Task task21 = new Task("(taskName21)", "(description)");
////        Epic epic2 = new Epic("(epicName2)", "(description)");
////        Subtask subtask2 = new Subtask("(subtaskName2)", "(description)", epic2);
//        Task task22 = new Task("(taskName22)", "(description)");
//
//        managerFromFile.createTask(task21);
////        managerFromFile.createEpic(epic2);
////        managerFromFile.createSubtask(subtask2);
////
////        managerFromFile.getTask(4);
////        managerFromFile.getEpic(8);
////        managerFromFile.getSubtask(3);
//        managerFromFile.createTask(task22);
//
//
//
//        for (Task task : manager.getAllTasks()) {
//            int id = task.getId();
//            Task taskFromFile = managerFromFile.getTask(id);
//            compare(task, taskFromFile);
//        }
//
//
//    } // end of main()
//
//    private static void compare(Task task, Task taskFromFile) {
//        if(task.equals(taskFromFile)) {
//            System.out.println("TRUE");
//        } else {
//            System.out.println("Ошибка. Задачи не равны");
//        }
//    }
//}
//
////        Task task1 = new Task("Shopping", "Coat, T-short, Cap", 0, Task.Status.NEW);
////        Task task2 = new Task("Buy food", "apples, bread, milk", 0, Task.Status.NEW);
////        Task task3 = new Task("Cooking", "Soap and spaghetti", 0, Task.Status.IN_PROGRESS);
////        Task task4 = new Task("Cleaning", "Kitchen and room", 0, Task.Status.DONE);
////        Task task5 = new Task("Wash the car", "in the morning", 0, Task.Status.NEW);
////        Task task6 = new Task("Play volleyball", "in the afternoon", 0, Task.Status.NEW);
////        Task task7 = new Task("Call Alex", "about summer", 0, Task.Status.NEW);
////        Task task8 = new Task("Order books", "not very much", 0, Task.Status.NEW);
////        Task task9 = new Task("Fix bike", "wheel", 0, Task.Status.NEW);
////
////
////        Epic epic1 = new Epic("Rest", "Eat, watch YouTube, sleep", 0);
////        Subtask subtask0 = new Subtask("Sleep", "well", 0, Task.Status.NEW, epic1);
////        Subtask subtask1 = new Subtask("Eat", "Taste something new", 0, Task.Status.NEW, epic1);
////        Subtask subtask2 = new Subtask("Watch YouTube", "Find something interesting", 0,
////                Task.Status.NEW, epic1);
////        Epic epic2 = new Epic("Become programmer", "And find a job", 0);
////
////        manager.createTask(task1);
////        manager.createTask(task2);
////        manager.createTask(task3);
////        manager.createTask(task4);
////        manager.createTask(task5);
////        manager.createTask(task6);
////        manager.createTask(task7);
////        manager.createTask(task8);
////        manager.createTask(task9);
////        manager.createEpic(epic1);
////        manager.createEpic(epic2);
////        manager.createSubtask(subtask0);
////        manager.createSubtask(subtask1);
////        manager.createSubtask(subtask2);
////
////        System.out.println("\nИстория просмотров до вызова задач (пустая):" + manager.getHistory());
////
////        manager.getTask(7);
////        manager.getTask(7);
////        manager.getTask(3);
////        manager.getTask(1);
////        manager.getTask(7);
////        manager.getTask(4);
////        manager.getTask(4);
////        manager.getTask(3);
////        manager.getTask(9);
////        System.out.println("\nИстория просмотров после первых вызовов (повторы удалены):" + manager.getHistory());
////
////        for (int i = 1; i <= 9; i++) {
////            manager.getTask(i);
////        }
////        manager.getTask(7);
////
////        for (int i = 10; i <= 11; i++) {
////            manager.getEpic(i);
////        }
////        for (int i = 12; i <= 14; i++) {
////            manager.getSubtask(i);
////        }
////
////        System.out.println("\nИстория просмотров (после новых вызовов задач):" + manager.getHistory());
////
////
////        manager.updateTask(new Task("Cooking (обновили статус)", "Soap and spaghetti", 3,
////                Task.Status.DONE));
////        manager.updateTask(new Task("Sleep (обновили задачу)", "a lot", 7, Task.Status.NEW));
////        Epic epic3 = new Epic("Write a story (обновили эпик)", "For me and friends", 10);
////        manager.updateEpic(epic3);
////        Subtask subtask4 = new Subtask("Create a plot (обновили подзадачу)", "with funny end", 12,
////                Task.Status.DONE, epic3);
////        manager.updateSubtask(subtask4);
////        System.out.println("\nИстория просмотров после изменения задач):\n"
////                + "(задачи в истории отображаются в измененном виде)" + manager.getHistory());
////
////        task3.setStatus(Task.Status.DONE);
////        manager.updateTask(task3);
////        manager.removeTask(8);
////        manager.removeEpic(10);
////        System.out.println("\nИстория просмотров после удаления некотрых задач\n" +
////                "удалены задача id 8, эпик id 10 (и его подзадачи id 12, 13, 14):" + manager.getHistory());
////
////        manager.getAllTasks();
////        manager.getAllSubtasks();
////        manager.getAllEpics();
////        manager.removeAllTasks();
////        manager.removeAllSubtasks();
////        manager.removeAllEpics();
////        System.out.println("\nИстория просмотров после удаления всех задач (пустая):" + manager.getHistory());
////    }
////}
