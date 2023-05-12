## Приложение "Библиотека"

### 1. Состав

Состоит из трех модулей:

- `shared` - java-модуль, содержащий интерфейсы и классы (модель, DTO и валидацию), совместно используемые несколькими
  модулями;
- `restservice` - REST-сервис, принимающий **реактивные** POST-запросы и извлекающий данные из запущенного экземпляра
  **реактивного** MondoDB;
- `webapp` - веб-приложение, принимающее **обычные** GET- и POST-запросы от браузера и использующее **реактивные**
  запросы к модулю `restservice` для получения необходимых данных.

### 2. Запуск

<details>
  <summary>Вариант 1 (предпочтительный): Docker-Compose.</summary>

Приложение докеризировано и запустить его можно с помощью [docker-compose](docker-compose.yaml):

````yaml
$ docker-compose up -d
  [ + ] Running 7/7
  ✔ webapp 4 layers [⣿⣿⣿⣿]      0B/0B      Pulled                                                                                                                                                                                  12.0s
  ✔ 38a980f2cc8a Already exists                                                                                                                                                                                                   0.0s
  ✔ de849f1cfbe6 Already exists                                                                                                                                                                                                   0.0s
  ✔ a7203ca35e75 Already exists                                                                                                                                                                                                   0.0s
  ✔ 9ac3613f881d Pull complete                                                                                                                                                                                                    9.2s
  ✔ restservice 1 layers [⣿]      0B/0B      Pulled                                                                                                                                                                                 9.6s
  ✔ d52bfd33d0d9 Pull complete                                                                                                                                                                                                    6.8s
  [ + ] Running 4/4
  ✔ Container docker-hoster  Running                                                                                                                                                                                                0.0s
  ✔ Container mongodb        Healthy                                                                                                                                                                                                0.6s
  ✔ Container webapp         Started                                                                                                                                                                                                0.9s
  ✔ Container restservice    Started                                                                                                                                                                                                0.9s
````

</details>

<details>
  <summary>Вариант 2: "java -jar".</summary>

JAR-артефакты модулей `restservice` и `webapp` являются исполняемыми, их следует собрать из корня проекта:

````shell
$ ./mvnw clean install
... 
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for library-application 1.0.0-SNAPSHOT:
[INFO] 
[INFO] library-application ................................ SUCCESS [  0.148 s]
[INFO] Shared ............................................. SUCCESS [  2.301 s]
[INFO] REST Service ....................................... SUCCESS [ 48.536 s]
[INFO] Web Application .................................... SUCCESS [  2.020 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
````

После сборки артефакты можно запустить. Модуль `restservice`:

````shell
$ cd restservice/target

$ java -jar restservice.jar

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.0.6)
...
````

и модуль `webapp`:

````shell
$ cd ../webapp/target

$ java -jar webapp.jar

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.0.6)
...
````

</details>

<details>
  <summary>Вариант 3: Конфигурация Intellij IDEA.</summary>

Запуcтить main-классы
[RestServiceApplication.java](restservice/src/main/java/ru/otus/restservice/RestServiceApplication.java) и
[WebappApplication.java](webapp/src/main/java/ru/otus/webapp/WebappApplication.java).

</details>

### 3. Доступ к приложению

Запущенное веб-приложение доступно на порту `8080`
([webapp: application.yaml](webapp/src/main/resources/application.yaml)):

![library-app.png](library-app.png)

> REST-сервис запускается на порту `8081`, MongoDb ожидается на стандартном порту `27017`
> ([restservice: application.yaml](restservice/src/main/resources/application.yaml)).

---
