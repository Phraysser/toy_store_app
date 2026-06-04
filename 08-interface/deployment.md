# Руководство по развёртыванию

## Системные требования

| Компонент | Требование |
|-----------|------------|
| JDK | 17 или выше |
| PostgreSQL | 14 или выше |
| Android | API 26+ (Android 8.0) |
| Android Studio | Hedgehog 2023.1+ |
| Maven/Gradle | 3.8+ / 8.x |
| RAM (сервер) | 512 MB минимум |

---

## 1. Настройка базы данных

```sql
-- Создать базу данных
CREATE DATABASE toystore;

-- Создать пользователя (опционально)
CREATE USER toystore_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE toystore TO toystore_user;
```

Выполнить DDL-скрипт:
```bash
psql -U postgres -d toystore -f ddl.sql
```

---

## 2. Настройка серверной части

### application.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/toystore
    username: postgres
    password: your_password
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

jwt:
  secret: ${JWT_SECRET:YourBase64EncodedSecretKeyAtLeast32CharactersLong}
  expiration: 3600000

server:
  port: 8080

springdoc:
  swagger-ui:
    path: /swagger-ui/index.html
  api-docs:
    path: /v3/api-docs
```

### Сборка и запуск

```bash
# Перейти в директорию сервера
cd toy-store-server

# Собрать JAR
./gradlew bootJar -x test

# Запустить
java -jar build/libs/toy-store-server-1.0.0.jar
```

### Проверка работоспособности

```bash
# Сервер запущен
curl http://localhost:8080/actuator/health
# {"status":"UP"}

# Swagger UI
# Открыть в браузере: http://localhost:8080/swagger-ui/index.html
```

---

## 3. Настройка мобильного приложения

### Изменить базовый URL

```kotlin
// NetworkUtils.kt или аналогичный файл
// Для локальной разработки:
const val BASE_URL = "http://10.0.2.2:8080/"  // эмулятор Android
// const val BASE_URL = "http://192.168.1.X:8080/"  // реальное устройство

// Для продакшена:
// const val BASE_URL = "https://your-server.com/"
```

### Сборка APK

1. Открыть проект в Android Studio
2. `Build → Build Bundle(s)/APK(s) → Build APK(s)`
3. APK находится по пути: `app/build/outputs/apk/debug/app-debug.apk`

### Установка на устройство

```bash
# Через ADB
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 4. Проверка полной интеграции

| Шаг | Действие | Ожидаемый результат |
|-----|----------|---------------------|
| 1 | Запустить PostgreSQL | БД принимает подключения |
| 2 | Запустить сервер | `{"status": "UP"}` на `/actuator/health` |
| 3 | Открыть приложение | Экран входа |
| 4 | Зарегистрироваться | Переход на главный экран (каталог) |
| 5 | Просмотреть каталог | Отображаются игрушки |
| 6 | Добавить товар в корзину | Товар появляется в корзине |
| 7 | Отключить интернет | Данные доступны из кэша Room |
| 8 | Включить интернет | Синхронизация происходит автоматически |

---

## 5. Конфигурация переменных окружения (для продакшена)

```bash
export DB_URL=jdbc:postgresql://db-host:5432/toystore
export DB_USERNAME=toystore_user
export DB_PASSWORD=secure_password
export JWT_SECRET=YourProductionSecretKeyAtLeast32Chars

java -jar toy-store-server.jar \
  --spring.datasource.url=$DB_URL \
  --spring.datasource.username=$DB_USERNAME \
  --spring.datasource.password=$DB_PASSWORD \
  --jwt.secret=$JWT_SECRET
