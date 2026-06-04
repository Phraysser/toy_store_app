# Курсовой проект: Мобильное приложение «Toy Store»

**Траектория В — Мобильная разработка (Android + Spring Boot Backend)**  
**Дисциплина:** Программная Инженерия  
**Институт:** СКФУ

---

## Описание проекта

**Toy Store** — мобильное Android-приложение для интернет-магазина игрушек. Позволяет просматривать каталог товаров, осуществлять поиск по категориям и названиям, добавлять товары в корзину и оформлять заказы. Данные синхронизируются с серверной частью через REST API с JWT-аутентификацией.

### Технологический стек

| Уровень | Технология |
|---|---|
| Мобильное приложение | Android (Kotlin), Jetpack Compose, Material Design 3, Retrofit2, Room |
| Серверная часть | Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA |
| База данных (сервер) | PostgreSQL |
| Аутентификация | JWT + BCrypt |
| Документация API | OpenAPI 3 (Swagger UI) |
| Архитектура | PCMEF |

---

## Структура документации

### [📁 Этап 0 — Инициация и бизнес-анализ](01-business-model/README.md) — 5%

| Документ | Описание |
|---|---|
| [Паспорт проекта](01-business-model/project-passport.md) | Цели, риски, KPI, стек технологий |
| [Бизнес-глоссарий](01-business-model/glossary.md) | 20 ключевых терминов предметной области |
| [IDEF0 A-0](<01-business-model/images/IDEF0 A-0.jpg>) | Диаграмма бизнес-контекста |
| [BUC-диаграмма](<01-business-model/images/BUC-диаграмма.jpg>) | Бизнес-прецеденты |
| [Бизнес-классы](<01-business-model/images/Безнес классы.jpg>) | Модель бизнес-классов |
| [Матрица стейкхолдеров](<01-business-model/images/Матрица стейкхолдеров.jpg>) | Заинтересованные стороны |
| [SWOT-анализ](<01-business-model/images/SWOT-анализ.jpg>) | Анализ текущего процесса покупки игрушек |

---

### [📁 Этап 1 — Проектирование требований](02-requirements/README.md) — 10%

| Документ | Описание |
|---|---|
| [Use Case диаграмма](02-requirements/use-case-diagram.md) | 10 прецедентов, 2 актора |
| [Domain Model](02-requirements/domain-model.md) | Сущности и их связи |
| [Спецификации прецедентов](02-requirements/use-case-specifications.md) | Детальное описание UC2, UC5 |
| [Расширенный глоссарий](02-requirements/glossary-extended.md) | 35 терминов |
| [Таблица трассировки](02-requirements/traceability-matrix.md) | Бизнес-цели → UC → статус реализации |

---

### [📁 Этап 2 — Архитектурное проектирование](03-architecture/README.md) — 10%

| Документ | Описание |
|---|---|
| [PCMEF-диаграмма](03-architecture/pcmef-diagram.md) | Слои, компоненты, правила зависимостей |
| [Описание слоёв PCMEF](03-architecture/Описание%20слоёв%20PCMEF-диаграммы.md) | Таблица слоёв и их компонентов |
| [Спецификация интерфейсов](03-architecture/interfaces.md) | IService, IRepository, REST-контракт |
| [Архитектурные решения (ADR)](03-architecture/adr.md) | 5 задокументированных ADR |

---

### [ Этап 3 — Проектирование базы данных](04-database/README.md) — 10%

| Документ | Описание |
|---|---|
| [ER-диаграмма + описание таблиц](04-database/README.md) | Логическая модель, маппинг JPA |
| [DDL-скрипты](04-database/ddl.sql) | Создание таблиц, индексов, ограничений PostgreSQL |

---

### [ Этап 4 — Детальное проектирование](05-detailed-design/README.md) — 10%

| Документ | Описание |
|---|---|
| [Диаграммы последовательности](05-detailed-design/sequence-diagrams.md) | 4 сценария: login, add-to-cart, catalog, checkout |
| [Диаграмма классов](05-detailed-design/class-diagram.md) | Детальная структура всех слоёв |
| [Спецификация методов](05-detailed-design/method-specs.md) | Сигнатуры ключевых методов |

---

### [📁 Этап 5 — Реализация ядра](06-implementation/README.md) — 15%

| Документ | Описание |
|---|---|
| [Entity-классы](06-implementation/core-entities.md) | User, Toy, Cart + DTO |
| [Сервисный слой](06-implementation/services.md) | UserService, ToyService, CartService, AuthService |
| [Тесты и покрытие](06-implementation/test-coverage.md) | JUnit 5 + JaCoCo (~44% покрытие) |

---

### [📁 Этап 6 — Рефакторинг и качество](07-refactoring/README.md) — 10%

| Документ | Описание |
|---|---|
| [Статический анализ](07-refactoring/static-analysis.md) | SonarQube, Android Lint — до/после |
| [Паттерны](07-refactoring/patterns.md) | Data Mapper, Identity Map, Lazy Load |
| [Журнал рефакторинга](07-refactoring/refactoring-log.md) | 8 задокументированных изменений |

---

### [ Этап 7 — Интерфейс](08-interface/README.md) — 15%

| Документ | Описание |
|---|---|
| [Мобильные экраны](08-interface/mobile-screens.md) | 7 экранов, Material Design 3, навигация |
| [REST API](08-interface/api-endpoints.md) | 13 эндпоинтов, OpenAPI/Swagger |
| [Безопасность](08-interface/security.md) | JWT, BCrypt, роли, CORS |
| [Развёртывание](08-interface/deployment.md) | Инструкция по запуску сервера и клиента |

---

### [ Этап 8 — Завершение](09-completion/README.md) — 15%

| Документ | Описание |
|---|---|
| [WBS](09-completion/wbs.md) | Иерархическая структура работ, ~180 ч |
| [Диаграмма Ганта](09-completion/gantt.md) | Календарный план 18 недель |
| [COCOMO](09-completion/cocomo.md) | Оценка трудозатрат (~3490 SLOC) |
| [Руководство пользователя](09-completion/user-guide.md) | Инструкция по работе с приложением |
| [Руководство администратора](09-completion/admin-guide.md) | Установка и настройка сервера |

---

## Реализованный функционал

- ✅ Регистрация и вход по username (JWT-аутентификация)
- ✅ Просмотр каталога игрушек в виде сетки (2 колонки)
- ✅ Поиск игрушек по названию
- ✅ Фильтрация товаров по категориям
- ✅ Просмотр деталей товара (изображение, описание, цена, наличие)
- ✅ Добавление товара в корзину
- ✅ Управление корзиной (изменение количества, удаление, очистка)
- ✅ Оформление заказа (checkout) с отображением реквизитов
- ✅ Экран настроек с переключением тёмной/светлой темы
- ✅ Выход из аккаунта
- ✅ 13 REST API эндпоинтов с документацией OpenAPI
- ✅ Синхронизация с серверной частью (Spring Boot + PostgreSQL)
- ✅ Оффлайн-режим (локальный Room-кэш)
- ✅ Административные функции (CRUD каталога, загрузка изображений)
- ✅ Модульные тесты (JUnit 5 + Mockito, покрытие ~44%)
- ✅ Интеграционные тесты репозиториев (@DataJpaTest)

