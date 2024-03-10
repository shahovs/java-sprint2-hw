TaskManager
Менеджер задач - таск-менеджер для управления сроками и задачами.
Cтек: Java 8+ (без фреймворков)

Менеджер задач позволяет создавать задачи, эпики (группа подзадач с общей целью) и подзадачи.
Хранение данных осущестляется в оперативном памяти, в файле или на сервере. 


Взаимодействие компонентов при использовании сервера:

(0) Подготовка:
Создаем и запускаем KVServer (первым)
Создаем и запускаем HttpTaskServer (вторым)
HttpTaskServer создает менеджера HttpTaskManager
HttpTaskManager создает KVTaskClient
KVTaskClient подключается к KVServer и регистрируется

HttpTaskManager получает исходное состояние менеджера (задачи, история) от KVTaskClient (из KVServer)


Новая задача создается в следующем порядке:

(1) Конечный пользователь (user/клиент, frontend, браузер, Insomnia и т.д.)
(например, httpClient в классе HttpTaskServerTest):
делаем задачу new Task(...), сереализуем ее в строку json toJson() и отправляем на сервер ->

-> ..(a) http-запрос 8080:tasks/... (body = задача в json).. ->

(2) HttpTaskServer получает http-запрос в json,
делает из него задачу (десериализует) fromJson()
и вызывает методы менеджера ->

-> ..(b) задача (manager.createTask(task)).. ->

(3) HttpTaskManager получает задачу (в качестве аргумента при вызове его метода createTask(task)),
кладет ее в свою мапу,
сохраняет свое состояние в json
и передает его в KVTaskClient (для передачи на KVServer)
    this.save() { kvTaskClient.put(String key, String json); } 
    this.load(String key) { kvTaskClient.load() } ->

-> ..(c) состояние менеджера в json (все задачи, история).. ->

(4) KVTaskClient получает состояние менеджера и отправляет на KVServer ->
(void this.put(String key, String json)    String this.load(String key))

-> ..(d) http-запрос 8078: состояние менеджера в json..->
(POST /save/<ключ>?API_TOKEN=    GET /load/<ключ>?API_TOKEN=)

(5) KVServer получает состояние менеджера в json и сохраняет json (String value) в свою мапу
