# REST API — Документация эндпоинтов

## Базовый URL

**Продакшен:** `https://your-server.com/api`  
**Локально:** `http://localhost:8080/api`

**Swagger UI:** `http://localhost:8080/swagger-ui/index.html`  
**OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

## Аутентификация

Все эндпоинты (кроме `/auth/**`) требуют заголовок:
```
Authorization: Bearer <JWT-TOKEN>
```

---

## Эндпоинты (13 штук — требование: 8+)

### Аутентификация (`/api/auth`)

#### POST /api/auth/register — Регистрация

**Запрос:**
```json
{
  "username": "john",
  "password": "secret123"
}
```

**Ответ 200 OK:**
```json
"User registered successfully"
```

**Ошибки:** 400 (невалидные данные), 409 (username уже занят)

---

#### POST /api/auth/login — Аутентификация

**Запрос:**
```json
{
  "username": "john",
  "password": "secret123"
}
```

**Ответ 200 OK:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john",
  "role": "USER"
}
```

**Ошибки:** 401 (неверные данные)

---

### Игрушки (`/api/toys`)

#### GET /api/toys — Список всех игрушек

**Ответ 200 OK:**
```json
[
  {
    "id": 1,
    "name": "Плюшевый мишка",
    "description": "Мягкая игрушка",
    "price": 1500.00,
    "category": "Мягкие игрушки",
    "imageUrl": "https://...",
    "stock": 10,
    "createdAt": "2026-05-01T10:00:00"
  }
]
```

---

#### GET /api/toys/{id} — Получить игрушку по ID

**Ответ 200 OK:** объект игрушки  
**Ошибки:** 404 (не найдена)

---

#### POST /api/toys — Создать игрушку (ADMIN only)

**Запрос:**
```json
{
  "name": "Конструктор LEGO",
  "description": "Набор 500 деталей",
  "price": 3500.00,
  "category": "Конструкторы",
  "stock": 5
}
```

**Ответ 201 Created:** объект созданной игрушки

---

#### PUT /api/toys/{id} — Обновить игрушку (ADMIN only)

**Запрос:**
```json
{
  "name": "Конструктор LEGO Premium",
  "price": 4000.00,
  "stock": 3
}
```

**Ответ 200 OK**

---

#### DELETE /api/toys/{id} — Удалить игрушку (ADMIN only)

**Ответ 200 OK**  
**Каскадно:** удаляет все связанные записи в корзинах

---

#### GET /api/toys/search?query=... — Поиск по названию или категории

**Пример:** `GET /api/toys/search?query=мишка`

**Ответ 200 OK:** список найденных игрушек

---

### Корзина (`/api/cart`)

#### GET /api/cart — Корзина текущего пользователя

**Ответ 200 OK:**
```json
[
  {
    "id": 1,
    "toyId": 5,
    "toyName": "Плюшевый мишка",
    "price": 1500.00,
    "quantity": 2,
    "total": 3000.00
  }
]
```

---

#### POST /api/cart/add?toyId={id}&quantity={qty} — Добавить в корзину

**Пример:** `POST /api/cart/add?toyId=5&quantity=2`

**Ответ 200 OK:** объект добавленной позиции

**Ошибки:** 409 (недостаточно товара на складе)

---

#### DELETE /api/cart/{id} — Удалить из корзины

**Ответ 200 OK**

---

#### DELETE /api/cart/clear — Очистить корзину

**Ответ 200 OK**

---

### Загрузка файлов (`/api/upload`)

#### POST /api/upload/image — Загрузка изображения (ADMIN only)

**Content-Type:** `multipart/form-data`

**Запрос:** файл изображения

**Ответ 200 OK:**
```json
{
  "imageUrl": "https://storage.example.com/images/toy-123.jpg"
}
```

---

## Таблица эндпоинтов

| # | Метод | URL | Описание | Авторизация |
|---|-------|-----|----------|-------------|
| 1 | POST | /api/auth/register | Регистрация | Нет |
| 2 | POST | /api/auth/login | Вход, получение токена | Нет |
| 3 | GET | /api/toys | Список всех игрушек | JWT |
| 4 | GET | /api/toys/{id} | Получить игрушку | JWT |
| 5 | POST | /api/toys | Создать игрушку | JWT (ADMIN) |
| 6 | PUT | /api/toys/{id} | Обновить игрушку | JWT (ADMIN) |
| 7 | DELETE | /api/toys/{id} | Удалить игрушку | JWT (ADMIN) |
| 8 | GET | /api/toys/search | Поиск | JWT |
| 9 | GET | /api/cart | Корзина | JWT |
| 10 | POST | /api/cart/add | Добавить в корзину | JWT |
| 11 | DELETE | /api/cart/{id} | Удалить из корзины | JWT |
| 12 | DELETE | /api/cart/clear | Очистить корзину | JWT |
| 13 | POST | /api/upload/image | Загрузка изображения | JWT (ADMIN) |

**Итого: 13 эндпоинтов** — требование 8+ выполнено ✅
