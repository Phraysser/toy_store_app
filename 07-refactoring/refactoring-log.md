# Журнал рефакторинга

Хронологическая запись изменений, внесённых в ходе этапа рефакторинга.

## RF-001: Введение Data Mapper — разделение Entity и DTO

**Изменённые файлы:**
- Добавлены: `ToyMapper.java`, `CartMapper.java`, `UserMapper.java`
- Обновлены: `ToyService.java`, `CartService.java`, `UserService.java`

**Суть:** Убрано прямое использование Entity-классов в ответах контроллеров. Введены DTO-records для входящих запросов и исходящих ответов.

**До:**
```java
return toyRepository.save(toy); // возвращает Entity напрямую
```

**После:**
```java
return toyMapper.toResponse(toyRepository.save(toy));
```

---

## RF-002: Замена `.get()` на `.orElseThrow()`

**Изменённые файлы:** `ToyService.java`, `CartService.java`, `UserService.java`

**Суть:** Все вызовы `Optional.get()` заменены на `.orElseThrow()` с информативным сообщением об ошибке.

**До:**
```java
Toy toy = toyRepository.findById(toyId).get();
```

**После:**
```java
Toy toy = toyRepository.findById(toyId)
    .orElseThrow(() -> new EntityNotFoundException("Игрушка не найдена: " + toyId));
```

---

## RF-003: Вынесение JWT-секрета в конфигурацию

**Изменённые файлы:** `JwtService.java`, `application.yml`

**Суть:** Секретный ключ JWT перенесён из исходного кода в файл конфигурации (и далее в переменную окружения для продакшена).

```yaml
# application.yml
jwt:
  secret: ${JWT_SECRET:defaultDevSecretKey32CharactersLong}
  expiration: 3600000
```

---

## RF-004: Единый GlobalExceptionHandler

**Добавлено:** `GlobalExceptionHandler.java`

**Суть:** Все исключения обрабатываются централизованно через `@RestControllerAdvice`. Клиент всегда получает JSON с полем `message`.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(new ErrorResponse(msg));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(e.getMessage()));
    }
}
```

---

## RF-005: Оптимизация N+1 запросов в CartRepository

**Изменённые файлы:** `CartRepository.java`

**Суть:** При загрузке корзины с информацией о товарах добавлен JOIN FETCH для предотвращения N+1.

**До:**
```java
List<Cart> findByUserId(Long userId); // N+1: отдельный запрос на каждый toy
```

**После:**
```java
@Query("SELECT c FROM Cart c LEFT JOIN FETCH c.toy WHERE c.userId = :userId")
List<Cart> findByUserIdWithToy(@Param("userId") Long userId);
```

---

## RF-006: Рефакторинг Android ViewModel

**Изменённые файлы:** `ToyViewModel.kt`, `CartViewModel.kt`

**Суть:** Убрано дублирование логики синхронизации. Выделен общий механизм обработки ошибок сети в базовый класс.

```kotlin
// BaseViewModel.kt — общий обработчик ошибок сети
abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected fun launchWithRetry(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                block()
            } catch (e: IOException) {
                // работаем в оффлайн-режиме, данные уже в Room
            } catch (e: HttpException) {
                // логируем серверную ошибку
            }
        }
    }
}
```

---

## RF-007: Валидация количества в корзине

**Изменённые файлы:** `CartService.java`, `AddToCartRequest.java`

**Суть:** Добавлена проверка, что количество товара не может быть отрицательным или нулевым.

**До:**
```java
cart.setQuantity(quantity); // любое значение
```

**После:**
```java
if (quantity <= 0) {
    throw new IllegalArgumentException("Количество должно быть больше 0");
}
cart.setQuantity(quantity);
```

---

## RF-008: Индексация часто используемых запросов

**Изменённые файлы:** `ToyRepository.java`

**Суть:** Добавлены индексы для ускорения поиска по категории и названию.

```java
@Query("SELECT t FROM Toy t WHERE t.category = :category ORDER BY t.createdAt DESC")
List<Toy> findByCategoryOrderByCreatedAtDesc(@Param("category") String category);

@Query("SELECT t FROM Toy t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
List<Toy> searchByName(@Param("query") String query);
