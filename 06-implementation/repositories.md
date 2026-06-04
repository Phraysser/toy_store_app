# Слой доступа к данным (Foundation)

Репозитории реализованы через Spring Data JPA — интерфейсы, расширяющие `JpaRepository`. Spring автоматически генерирует реализацию по именам методов и аннотации `@Query`.

Каждый репозиторий отвечает ровно за одну таблицу. Бизнес-логика (проверки прав, транзакции) — строго в сервисном слое.

## UserRepository

Отвечает за таблицу `users`. Используется `UserService` для регистрации, аутентификации и управления профилем.

```java
package com.toystore.repository;

import com.toystore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Поиск по username — используется при аутентификации. */
    Optional<User> findByUsername(String username);

    /** Проверка занятости username перед регистрацией. */
    boolean existsByUsername(String username);

    /** Поиск по username с учётом роли — для проверки прав. */
    Optional<User> findByUsernameAndRole(String username, String role);
}
```

### Используемые методы

| Метод | Вызывается из | Назначение |
|-------|---------------|------------|
| findByUsername(username) | UserService.authenticate() | Найти пользователя для входа |
| existsByUsername(username) | UserService.registerUser() | Проверить уникальность логина |
| findById(id) | UserService.getProfile() | Загрузить профиль по id из JWT |
| save(user) | UserService.registerUser() | Сохранить нового пользователя |

## ToyRepository

Отвечает за таблицу `toys`. Все запросы поддерживают фильтрацию по категории и поиск по названию.

```java
package com.toystore.repository;

import com.toystore.entity.Toy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToyRepository extends JpaRepository<Toy, Long> {

    /**
     * Все игрушки в категории, отсортированные по дате добавления.
     * Основной запрос для экрана каталога с фильтром.
     */
    List<Toy> findByCategoryOrderByCreatedAtDesc(String category);

    /**
     * Поиск игрушек по названию (case-insensitive, частичное совпадение).
     * Используется для поиска в каталоге.
     */
    List<Toy> findByNameContainingIgnoreCase(String name);

    /**
     * Игрушки с остатком на складе больше заданного.
     * Используется для отображения только доступных товаров.
     */
    List<Toy> findByStockGreaterThan(Integer stock);

    /**
     * Игрушки в категории с остатком на складе.
     * Комбинированный запрос для фильтрации.
     */
    List<Toy> findByCategoryAndStockGreaterThan(String category, Integer stock);

    /**
     * Поиск по названию ИЛИ категории.
     * Используется для универсального поиска.
     */
    @Query("""
        SELECT t FROM Toy t
        WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(t.category) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<Toy> searchByNameOrCategory(String query);
}
```

### Используемые методы

| Метод | Вызывается из | Назначение |
|-------|---------------|------------|
| findAll() | ToyService.getAllToys() | Получить все игрушки |
| findByCategory(category) | ToyService.getAllToys(cat) | Фильтрация по категории |
| findByNameContainingIgnoreCase(name) | ToyService.searchToys() | Поиск по названию |
| findById(id) | ToyService.getToyById() | Получить игрушку по ID |
| findByStockGreaterThan(0) | ToyService.getAvailableToys() | Только в наличии |
| save(toy) | ToyService.createToy(), updateToy() | Создание / обновление |
| delete(toy) | ToyService.deleteToy() | Удаление (CASCADE удаляет carts) |

## CartRepository

Отвечает за таблицу `carts`. Запросы всегда фильтруют по `userId` — пользователь видит только свою корзину.

```java
package com.toystore.repository;

import com.toystore.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Все позиции корзины пользователя.
     * Основной запрос для экрана корзины.
     */
    List<Cart> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Позиция корзины по пользователю и игрушке.
     * Используется для проверки: есть ли уже товар в корзине.
     */
    Optional<Cart> findByUserIdAndToyId(Long userId, Long toyId);

    /**
     * Удаление всех позиций корзины пользователя.
     * Используется при очистке корзины.
     */
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.userId = :userId")
    void deleteByUserId(Long userId);

    /**
     * Проверка: есть ли у пользователя хоть одна позиция в корзине.
     */
    boolean existsByUserId(Long userId);

    /**
     * Количество позиций в корзине пользователя.
     * Используется для отображения бейджа на иконке корзины.
     */
    long countByUserId(Long userId);
}
```

### Используемые методы

| Метод | Вызывается из | Назначение |
|-------|---------------|------------|
| findByUserId(userId) | CartService.getCartByUser() | Экран корзины |
| findByUserIdAndToyId(userId, toyId) | CartService.addToCart() | Проверка дубликата |
| deleteByUserId(userId) | CartService.clearCart() | Очистка корзины |
| countByUserId(userId) | CartService.getCartCount() | Бейдж корзины |
| save(cart) | CartService.addToCart() | Добавление / обновление |
| delete(cart) | CartService.removeFromCart() | Удаление позиции |

## Архитектурные правила работы с репозиториями

| Правило | Обоснование |
|---------|-------------|
| Репозитории — только интерфейсы, без `@Component` логики | Единственная ответственность: доступ к данным |
| Все запросы фильтруют по `userId` | Изоляция данных пользователей, безопасность |
| `findByUserIdAndToyId` вместо `findById` | Исключает IDOR-уязвимость (доступ к чужим данным) |
| `@Modifying` + `@Transactional` на bulk-операциях | Корректная работа с JPA кэшем при UPDATE/DELETE |
| `JOIN FETCH` вместо `FetchType.EAGER` | Устранение N+1 запросов, контроль точек загрузки |
| Каскадное удаление через `ON DELETE CASCADE` в DDL | Гарантированная целостность даже при прямых SQL-операциях |

## Взаимодействие репозиториев и сервисов

```
ToyController
       │
       ▼
ToyService ──── ToyRepository ──── PostgreSQL: toys

CartController
       │
       ▼
CartService ──── CartRepository ─── PostgreSQL: carts
       │
       └──────────── ToyRepository   ──── PostgreSQL: toys (для проверки stock)

AuthController
       │
       ▼
UserService ────────── UserRepository ──────── PostgreSQL: users
```
