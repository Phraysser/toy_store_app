# Описание слоёв PCMEF-диаграммы

## Таблица слоёв

| Слой | Расположение | Ответственность | Компоненты |
|------|--------------|-----------------|------------|
| Presentation | Мобильное устройство | Отображение данных, обработка пользовательского ввода, локальное кэширование | Activity, ViewModel, Jetpack Compose Screens, Room Database |
| Control | Сервер | Обработка HTTP-запросов, валидация входных данных, маршрутизация к сервисам | `@RestController` классы: AuthController, ToyController, CartController, FileController |
| Mediator | Сервер | Бизнес-логика: проверка прав, бизнес-правила, оркестрация операций | `@Service` классы: UserService, ToyService, CartService, AuthService |
| Entity | Сервер | Представление бизнес-сущностей с поведением, отображение на таблицы БД | `@Entity` классы: User, Toy, Cart |
| Foundation | Сервер | Доступ к базе данных, маппинг объектов через ORM | `@Repository` интерфейсы: UserRepository, ToyRepository, CartRepository |

## Правила зависимостей

Зависимости направлены строго сверху вниз:
- Control зависит от Mediator (через интерфейсы сервисов `IXxxService`).
- Mediator зависит от Entity и Foundation (через интерфейсы репозиториев).
- Foundation зависит от Entity и базы данных (PostgreSQL).
- Обратных зависимостей нет. Presentation взаимодействует с сервером только через REST API — нет прямых зависимостей от серверных классов.

## Распределение компонентов реального проекта

### Клиентская часть (Presentation)

| Компонент | Класс | Роль |
|-----------|-------|------|
| Экран списка игрушек | ToyListScreen | Отображение каталога товаров |
| Экран деталей товара | ToyDetailScreen | Просмотр информации об игрушке |
| Экран корзины | CartScreen | Управление корзиной покупок |
| ViewModel игрушек | ToyViewModel | Управление состоянием каталога |
| ViewModel корзины | CartViewModel | Управление состоянием корзины |
| HTTP-клиент | RetrofitClient | Синглтон Retrofit для API-запросов |
| Локальная БД | Room Database | Кэширование каталога и корзины |

### Серверная часть (Control → Foundation)

| Слой | Класс | Роль |
|------|-------|------|
| Control | AuthController | POST /api/auth/login, /register |
| Control | ToyController | CRUD /api/toys |
| Control | CartController | CRUD /api/cart |
| Control | FileController | POST /api/upload/image |
| Mediator | UserService | Регистрация, аутентификация пользователей |
| Mediator | ToyService | Бизнес-логика работы с каталогом |
| Mediator | CartService | Бизнес-логика работы с корзиной |
| Mediator | AuthService | Генерация и валидация JWT-токенов |
| Entity | User, Toy, Cart | JPA-сущности |
| Foundation | UserRepository | Запросы к таблице users |
| Foundation | ToyRepository | Запросы к таблице toys |
| Foundation | CartRepository | Запросы к таблице carts |