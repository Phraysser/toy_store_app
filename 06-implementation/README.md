
# Этап 5: Реализация ядра (Недели 11–12)

## Цель этапа

Полная реализация слоёв Entity и Foundation серверной части, реализация ключевых Use Cases в Mediator (Service), модульное тестирование с покрытием > 40%.

## Результаты

| Артефакт | Описание | Документ |
|----------|----------|----------|
| Entity-классы | JPA-сущности с бизнес-методами | [core-entities.md](core-entities.md) |
| Сервисный слой | Бизнес-логика, транзакции | [services.md](services.md) |
| Слой доступа к данным | Репозитории, запросы | [repositories.md](repositories.md) |
| Модульные тесты | JUnit-тесты, отчёт JaCoCo | [test-coverage.md](test-coverage.md) |

## Структура серверного проекта

```
toy-store-server/
── src/main/java/com/toystore/
│   ├── config/
│   │   ├── SecurityConfig.java         ← Spring Security + JWT-фильтр
│   │   └── OpenApiConfig.java          ← Swagger UI конфигурация
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── ToyController.java
│   │   ├── CartController.java
│   │   └── FileController.java
│   ├── service/
│   │   ├── UserService.java
│   │   ├── ToyService.java
│   │   ├── CartService.java
│   │   └── AuthService.java
│   ├── security/
│   │   ├── JwtService.java             ← генерация и валидация JWT
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── UserDetailsImpl.java
│   │   └── UserDetailsServiceImpl.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── ToyRepository.java
│   │   └── CartRepository.java
│   ├── entity/
│   │   ├── User.java
│   │   ├── Toy.java
│   │   ├── Cart.java
│   │   └── enums/
│   │       ── UserRole.java           ← USER, ADMIN
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── JwtResponse.java
│   │   ├── ToyResponse.java
│   │   └── CartResponse.java
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       ├── EntityNotFoundException.java
│       ├── InsufficientStockException.java
│       └── UsernameAlreadyExistsException.java
├── src/main/resources/
│   └── application.yml                 ← БД, JWT-секрет, Swagger
└── src/test/java/com/toystore/
├── service/
│   ├── UserServiceTest.java
│   ├── ToyServiceTest.java
│   └── CartServiceTest.java
└── repository/
├── ToyRepositoryTest.java
└── CartRepositoryTest.java
```

## Выполненные требования траектории В

| Требование | Статус |
|------------|--------|
| Мобильное приложение с 5+ экранами | ✅ 7 экранов |
| Серверная часть на Java (Spring Boot) | ✅ |
| REST API (8+ эндпоинтов) | ✅ 13 эндпоинтов |
| Документация OpenAPI (Swagger UI) | ✅ |
| Аутентификация через JWT | ✅ |
| Локальное кэширование (оффлайн-режим) | ✅ Room Database |
| Модульное тестирование (покрытие > 40%) | ✅ 44% |
