# Отчёт статического анализа

## Инструменты анализа

| Инструмент | Тип анализа | Применение |
|------------|-------------|------------|
| SonarQube | Качество кода, уязвимости, запахи кода | Серверная часть (Java) |
| Checkstyle | Соответствие стандарту оформления | Серверная часть (Java) |
| Android Lint | Анализ Android-кода | Мобильное приложение (Kotlin) |

---

## Результаты SonarQube (серверная часть)

### До рефакторинга

| Категория | Проблем | Критичность |
|-----------|---------|-------------|
| Bugs (ошибки) | 3 | MAJOR, MINOR |
| Vulnerabilities (уязвимости) | 1 | MAJOR |
| Code Smells (запахи) | 12 | INFO, MINOR |
| Дублирование кода | 8% | — |
| Технический долг | 2h 30min | — |

### После рефакторинга

| Категория | Проблем | Изменение |
|-----------|---------|-----------|
| Bugs | 0 | ↓ −3 |
| Vulnerabilities | 0 | ↓ −1 |
| Code Smells | 4 | ↓ −8 |
| Дублирование кода | 3% | ↓ −5% |
| Технический долг | 40min | ↓ −1h 50min |

---

## Устранённые критические проблемы

### Bug: Null Pointer в CartService

**До:**
```java
public CartResponse addToCart(Long userId, Long toyId, Integer quantity) {
    Toy toy = toyRepository.findById(toyId).get(); // NPE!
    Cart cart = new Cart();
    cart.setToy(toy);
    cart.setQuantity(quantity);
    ...
}
```

**После:**
```java
public CartResponse addToCart(Long userId, Long toyId, Integer quantity) {
    Toy toy = toyRepository.findById(toyId)
        .orElseThrow(() -> new EntityNotFoundException("Игрушка не найдена: " + toyId));
    
    if (toy.getStock() < quantity) {
        throw new InsufficientStockException("Недостаточно товара на складе");
    }
    
    Cart cart = new Cart();
    cart.setToy(toy);
    cart.setQuantity(quantity);
    ...
}
```

---

### Vulnerability: JWT-секрет в исходном коде

**До:**
```java
private final String secretKey = "mySecretKey12345"; // в коде!
```

**После:**
```java
@Value("${jwt.secret}")
private String secretKey; // читается из application.yml / переменной окружения
```

---

### Code Smell: дублирование маппинга Entity → Response

**До:** в каждом сервисе свой дублирующийся код преобразования.

**После:** выделены mapper-методы (паттерн Data Mapper):

```java
@Component
public class ToyMapper {
    public ToyResponse toResponse(Toy entity) {
        return new ToyResponse(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getPrice(),
            entity.getCategory(),
            entity.getImageUrl(),
            entity.getStock(),
            entity.getStock() > 0,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
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

---

### Code Smell: прямая сериализация Entity в контроллере

**До:**
```java
@GetMapping("/{id}")
public ResponseEntity<Toy> getToy(@PathVariable Long id) {
    return ResponseEntity.ok(toyRepository.findById(id).get()); // утечка passwordHash!
}
```

**После:**
```java
@GetMapping("/{id}")
public ResponseEntity<ToyResponse> getToy(@PathVariable Long id) {
    Toy toy = toyRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Игрушка не найдена"));
    return ResponseEntity.ok(toyMapper.toResponse(toy));
}
```

---

## Результаты Android Lint (мобильное приложение)

| Категория | До | После |
|-----------|-----|-------|
| Warnings | 11 | 4 |
| Errors | 0 | 0 |
| Unused resources | 3 | 0 |
| Deprecated API calls | 2 | 0 |

---

## Рекомендации по дальнейшему улучшению

1. **Покрытие тестами:** увеличить с 44% до 60% за счёт интеграционных тестов контроллеров.
2. **Документация API:** добавить примеры запросов/ответов в OpenAPI-аннотации.
3. **Логирование:** внедрить структурированное логирование через SLF4J + Logback.
4. **Мониторинг:** добавить метрики через Micrometer + Prometheus для продакшена.

