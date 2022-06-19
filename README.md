# java-sprint2-hw


/**
* ###################################
* http взаимодействие компонентов
* 
* Новая задача передается по пути:
* 
* (1) Конечный пользователь (/user/клиент/frontend/браузер/Insomnia)
* (у меня это httpClient в классе HttpTaskServerTest):
* делаем задачу (new Task(...)), переоформляем ее в json (toJson()) и отправляем на сервер ->
* 
* -> ..(a) http-запрос8080:tasks/... (body = задача в json).. ->
* 
* (2) HttpTaskServer (при первом запуске создает менеджера HttpTaskManager,
* далее получает http-запрос в json, делает из него задачу (десериализует)(fromJson()) и вызывает методы менеджера ->
* 
* -> ..(b) задача (manager.createTask(task)).. ->
* 
* (3) HttpTaskManager вначале создает KVTaskClient и получает исходное состояние от KVTaskClient (из KVServer),
* далее получает задачу (в качестве аргумента при вызове его метода createTask(task)), кладет ее в свою мапу, 
* сохраняет свое состояние в json и передает его в KVTaskClient (для передачи на KVServer)
*     this.save() { kvTaskClient.put(String key, String json); } 
*     this.load(String key) { kvTaskClient.load() } ->
* 
* -> ..(c) состояние менеджера в json (все задачи, история).. ->
* 
* (4) KVTaskClient получает состояние менеджера и отправляет на KVServer ->
* (void this.put(String key, String json)    String this.load(String key))
* 
* -> ..(d) http-запрос8078: состояние менеджера в json..->
* (POST /save/<ключ>?API_TOKEN=    GET /load/<ключ>?API_TOKEN=)
* 
* (5) KVServer получает состояние менеджера в json и сохраняет json (String value) в свою мапу
* 
* Оба сервера создаем и запускаем сразу, поскольку предполагается, что они должны работать к началу работы с задачами
* (то есть должны работать всегда).
* 
* HttpTaskServer создает HttpTaskManager
* HttpTaskManager создает KVTaskClient
* ###################################
*/