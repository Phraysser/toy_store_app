# Спецификация интерфейсов между слоями

Для обеспечения слабой связности и тестируемости слои взаимодействуют через интерфейсы. Это позволяет подменять реализации (например, для тестирования с использованием mock-объектов).

---

## 1. Интерфейсы уровня Mediator (бизнес-логика)

### IUserService

```java
public interface IUserService {
    User register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    User getUserById(Long id);
    User getCurrentUser();
}
```

### IToyService

```java
public interface IToyService {
    Toy createToy(CreateToyRequest request);
    Toy updateToy(Long id, UpdateToyRequest request);
    void deleteToy(Long id);

    Toy getToyById(Long id);
    List<Toy> getAllToys();

    List<Toy> searchToys(String query);
    List<Toy> getByCategory(String category);
}
```

### ICartService

```java
public interface ICartService {
    Cart addToCart(Long userId, Long toyId, Integer quantity);
    List<Cart> getCart(Long userId);

    Cart updateQuantity(Long cartId, Integer quantity);

    void removeFromCart(Long cartId);
    void clearCart(Long userId);

    BigDecimal calculateTotal(Long userId);
}
```

### IFileService

```java
public interface IFileService {
    String uploadImage(MultipartFile file);
    void deleteImage(String imageUrl);
}
```

---

## 2. Интерфейсы уровня Foundation (репозитории)

Spring Data JPA автоматически генерирует реализации по сигнатуре методов.

### UserRepository

```java
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
```

### ToyRepository

```java
public interface ToyRepository extends JpaRepository<Toy, Long> {

    List<Toy> findByCategory(String category);

    List<Toy> findByNameContainingIgnoreCase(String name);

    List<Toy> findByStockGreaterThan(Integer stock);
}
```

### CartRepository

```java
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
```

---

## 3. Интерфейсы REST API (контракт клиент-сервер)

| Метод  | Endpoint                  | Описание                      | Авторизация |
| ------ | ------------------------- | ----------------------------- | ----------- |
| POST   | `/api/auth/register`      | Регистрация пользователя      | Нет         |
| POST   | `/api/auth/login`         | Вход и получение JWT          | Нет         |
| GET    | `/api/toys`               | Получить список игрушек       | Нет         |
| GET    | `/api/toys/{id}`          | Получить игрушку по ID        | Нет         |
| GET    | `/api/toys/search?query=` | Поиск игрушек                 | Нет         |
| POST   | `/api/toys`               | Создать игрушку               | ADMIN       |
| PUT    | `/api/toys/{id}`          | Обновить игрушку              | ADMIN       |
| DELETE | `/api/toys/{id}`          | Удалить игрушку               | ADMIN       |
| GET    | `/api/cart`               | Получить корзину пользователя | JWT         |
| POST   | `/api/cart/add`           | Добавить товар в корзину      | JWT         |
| DELETE | `/api/cart/{id}`          | Удалить товар из корзины      | JWT         |
| DELETE | `/api/cart/clear`         | Очистить корзину              | JWT         |
| POST   | `/api/upload/image`       | Загрузка изображения          | ADMIN       |

```
```
