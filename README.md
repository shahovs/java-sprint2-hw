# java-sprint2-hw
TaskManager

ЧТО ОСТАЛОСЬ СДЕЛАТЬ
- HttpTaskManager получает исходное состояние менеджера (задачи, история) от KVTaskClient (из KVServer)
- обработку путей subtask
- 

###################################
http взаимодействие компонентов

(0) Подготовка:
Создаем и запускаем KVServer (первым!)
Создаем и запускаем HttpTaskServer (вторым)
HttpTaskServer создает менеджера HttpTaskManager
HttpTaskManager создает KVTaskClient
KVTaskClient подключается к KVServer и регистрируется
HttpTaskManager получает исходное состояние менеджера (задачи, история) от KVTaskClient (из KVServer)

Новая задача передается по пути:

(1) Конечный пользователь (/user/клиент/frontend/браузер/Insomnia)
(например, httpClient в классе HttpTaskServerTest или в HttpMain):
делаем задачу new Task(...), переоформляем ее в json toJson() и отправляем на сервер ->

-> ..(a) http-запрос8080:tasks/... (body = задача в json).. ->

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

-> ..(d) http-запрос8078: состояние менеджера в json..->
(POST /save/<ключ>?API_TOKEN=    GET /load/<ключ>?API_TOKEN=)

(5) KVServer получает состояние менеджера в json и сохраняет json (String value) в свою мапу
