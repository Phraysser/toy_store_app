# Применённые паттерны рефакторинга

## Data Mapper

**Назначение (из методички)**

Отделение бизнес-логики (Entity) от логики доступа к данным. Entity-классы не должны содержать SQL-код или знать о структуре БД.

**Проблема (до рефакторинга)**

Entity-классы напрямую использовались как DTO в ответах REST API — клиент получал внутренние поля (`passwordHash`, `updatedAt`, `@ManyToOne`-ссылки с циклическими зависимостями).

**Решение**

Введены отдельные классы-маппер (`ToyMapper`, `CartMapper`, `UserMapper`) и DTO-records для запросов/ответов.

```java
// Маппер разделяет знания Entity и контракт REST API
@Component
public class ToyMapper {

    public ToyResponse toResponse(Toy toy) {
        return new ToyResponse(
            toy.getId(),
            toy.getName(),
            toy.getDescription(),
            toy.getPrice(),
            toy.getCategory(),
            toy.getImageUrl(),
            toy.getStock(),
            toy.getStock() > 0,
            toy.getCreatedAt(),
            toy.getUpdatedAt()
        );
    }

    public Toy toEntity(CreateToyRequest request) {
        Toy toy = new Toy();
        toy.setName(request.name());
        toy.setDescription(request.description());
        toy.setPrice(request.price());
        toy.setCategory(request.category());
        toy.setImageUrl(request.imageUrl());
        toy.setStock(request.stock());
        return toy;
    }
}
```

**Результат:** Entity-классы не зависят от REST-контракта. Изменение структуры ответа API не требует изменения Entity.

---

## Identity Map

**Назначение**

Обеспечение уникальности объектов в сессии: одна и та же строка БД всегда представлена одним объектом в памяти. Предотвращает дублирование объектов и несогласованность данных.

**Реализация**

В проекте Identity Map реализован через JPA First-Level Cache (кэш первого уровня Hibernate). В рамках одной транзакции (`@Transactional`) каждый объект Entity загружается из БД ровно один раз и кэшируется в `EntityManager`.

```java
@Transactional
public void demonstrateIdentityMap() {
    // Оба вызова возвращают ОДИН и тот же объект из кэша EntityManager
    Toy toy1 = toyRepository.findById(1L).get();
    Toy toy2 = toyRepository.findById(1L).get();

    System.out.println(toy1 == toy2); // true — Identity Map!
}
```

**Дополнительная реализация: кэш на Android**

На клиенте Identity Map реализован через `Room Database` — локальная база выступает как единый источник истины для UI.

---

## Lazy Load

**Назначение**

Отложенная загрузка связанных объектов: связанные коллекции загружаются только при первом обращении, а не при загрузке родительской сущности.

**Реализация**

```java
// Toy НЕ загружает все Cart при запросе каталога
@OneToMany(mappedBy = "toy", fetch = FetchType.LAZY)
private List<Cart> carts;

// User НЕ загружает все Cart при аутентификации
@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
private List<Cart> carts;
```

**Проблема N+1:** при неаккуратном использовании Lazy Load может вызвать N дополнительных SQL-запросов. Решение — использование `@EntityGraph` или JPQL JOIN FETCH для сценариев, требующих данных коллекции.

```java
// Используем JOIN FETCH при необходимости
@Query("SELECT t FROM Toy t LEFT JOIN FETCH t.carts WHERE t.id = :toyId")
Optional<Toy> findByIdWithCarts(@Param("toyId") Long toyId);
```

---

## Strategy Pattern (для обработки ошибок)

**Назначение**

Определение семейства алгоритмов, инкапсуляция каждого из них и обеспечение их взаимозаменяемости.

**Реализация**

В проекте Strategy Pattern используется в `GlobalExceptionHandler` для централизованной обработки различных типов исключений.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(e.getMessage()));
    }

    // Другие обработчики...
}
```

**Результат:** Контроллеры не содержат логику обработки ошибок. Все исключения обрабатываются единообразно.
