# java-sprint2-hw
Second sprint homework


//    static String toString(HistoryManager manager){}
//    private static List<Integer> fromString(String line) {
//        List<Integer> ids = new ArrayList<>();
//        if (line == null || line.isBlank()) {
//            return ids;
//        }
//        String[] integers = line.split(",");
//        for (String id : integers) {
//            ids.add(Integer.parseInt(id));
//        }
//        return ids;
//    }

//    private static Task fromString(String line) {
//        if (line.startsWith("id") || line.isBlank()) {
//            System.out.println("Ошибка. Неправильная строка.");
//            return new Task("", "");
//        }
//
//        String[] elements = line.split(",");
//        if (elements.length < 5) {
//            System.out.println("Ошибка. Элементов в строке недостаточно");
//            return new Task("", "");
//        }
//
//        int id = Integer.parseInt(elements[0]);
//        TypesOfTasks type = TypesOfTasks.valueOf(elements[1]);
//        String name = elements[2];
//        Task.Status status = Task.Status.valueOf(elements[3]);
//        String description = elements[4];
//
//        Task result = new Task("", "");
//        switch (type) {
//            case TASK:
//                result = new Task(name, description, id, status);
//                break;
//            case EPIC:
//                result = new Epic(name, description, id);
//                break;
//            case SUBTASK:
//                if (elements.length == 6) {
//                    int idEpic = Integer.parseInt(elements[5]); //System.out.println("IDEPIC: " + idEpic);
//                    //Epic epic = manager.getEpic(idEpic);
//                    result = new Subtask(name, description, id, status, idEpic);
//                } else {
//                    System.out.println("Ошибка. У подзадачи не найден id эпика.");
//                }
//                break;
//            default:
//                System.out.println("Ошибка. Не найдет тип задачи.");
//        }
//        return result;
//    }