# Спецификация методов

Сигнатуры и контракты ключевых методов по каждому слою архитектуры.

## Control — REST-контроллеры

### AuthController

```java
/**
 * POST /api/auth/login
 * Аутентификация пользователя. Возвращает JWT-токен.
 */
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request);

/**
 * POST /api/auth/register
 * Регистрация нового пользователя.
 */
@PostMapping("/register")
public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request);
```

### ToyController

```java
/**
 * GET /api/toys
 * Получение всех игрушек с возможностью фильтрации по категории.
 */
@GetMapping
public ResponseEntity<List<ToyResponse>> getAll(
    @RequestParam(required = false) String category
);

/**
 * GET /api/toys/{id}
 * Получение игрушки по ID.
 */
@GetMapping("/{id}")
public ResponseEntity<ToyResponse> getById(@PathVariable Long id);

/**
 * POST /api/toys
 * Создание новой игрушки (только ADMIN).
 */
@PostMapping
public ResponseEntity<ToyResponse> create(
    @Valid @RequestBody CreateToyRequest request,
    Principal principal
);

/**
 * PUT /api/toys/{id}
 * Обновление игрушки (только ADMIN).
 */
@PutMapping("/{id}")
public ResponseEntity<ToyResponse> update(
    @PathVariable Long id,
    @Valid @RequestBody UpdateToyRequest request,
    Principal principal
);

/**
 * DELETE /api/toys/{id}
 * Удаление игрушки (только ADMIN).
 */
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal);

/**
 * GET /api/toys/search
 * Поиск игрушек по названию или категории.
 */
@GetMapping("/search")
public ResponseEntity<List<ToyResponse>> search(
    @RequestParam String query
);
```

### CartController

```java
/**
 * GET /api/cart
 * Получение корзины текущего пользователя.
 */
@GetMapping
public ResponseEntity<List<CartResponse>> getCart(Principal principal);

/**
 * POST /api/cart/add
 * Добавление товара в корзину.
 */
@PostMapping("/add")
public ResponseEntity<CartResponse> addToCart(
    @RequestParam Long toyId,
    @RequestParam Integer quantity,
    Principal principal
);

/**
 * DELETE /api/cart/{id}
 * Удаление позиции из корзины.
 */
@DeleteMapping("/{id}")
public ResponseEntity<Void> removeFromCart(
    @PathVariable Long id,
    Principal principal
);

/**
 * DELETE /api/cart/clear
 * Полная очистка корзины.
 */
@DeleteMapping("/clear")
public ResponseEntity<Void> clearCart(Principal principal);
```

## Mediator — сервисы бизнес-логики

### IUserService

```java
public interface IUserService {

    /**
     * Регистрация нового пользователя.
     * @throws UsernameAlreadyExistsException если username занят
     */
    UserResponse registerUser(RegisterRequest request);

    /**
     * Аутентификация пользователя по username и паролю.
     * @throws AuthenticationException если учётные данные неверны
     * @return JWT-токен и информация о пользователе
     */
    AuthResponse authenticate(LoginRequest request);

    /**
     * Получение профиля по ID.
     * @throws EntityNotFoundException если пользователь не найден
     */
    UserResponse getUserById(Long id);
}
```

### IToyService

```java
public interface IToyService {

    /**
     * Создание новой игрушки.
     * @param userId — идентификатор администратора
     */
    ToyResponse createToy(Long userId, CreateToyRequest request);

    /**
     * Получение всех игрушек с фильтрацией по категории.
     */
    List<ToyResponse> getAllToys(String category);

    /**
     * Получение игрушки по ID.
     * @throws EntityNotFoundException если игрушка не найдена
     */
    ToyResponse getToyById(Long id);

    /**
     * Поиск игрушек по названию (case-insensitive).
     */
    List<ToyResponse> searchToys(String query);

    /**
     * Обновление игрушки.
     * @throws AccessDeniedException если пользователь не ADMIN
     */
    ToyResponse updateToy(Long id, Long userId, UpdateToyRequest request);

    /**
     * Удаление игрушки.
     * @throws AccessDeniedException если пользователь не ADMIN
     */
    void deleteToy(Long id, Long userId);
}
```

### ICartService

```java
public interface ICartService {

    /**
     * Добавление товара в корзину.
     * Если товар уже есть — увеличивает количество.
     * @throws EntityNotFoundException если товар не найден
     * @throws InsufficientStockException если недостаточно товара на складе
     */
    CartResponse addToCart(Long userId, Long toyId, Integer quantity);

    /**
     * Получение корзины пользователя.
     */
    List<CartResponse> getCartByUser(Long userId);

    /**
     * Удаление позиции из корзины.
     */
    void removeFromCart(Long userId, Long cartItemId);

    /**
     * Полная очистка корзины.
     */
    void clearCart(Long userId);

    /**
     * Расчёт итоговой суммы корзины.
     */
    BigDecimal calculateTotal(Long userId);
}
```

## Foundation — репозитории

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}

public interface ToyRepository extends JpaRepository<Toy, Long> {
    List<Toy> findByCategory(String category);
    List<Toy> findByNameContainingIgnoreCase(String name);
    List<Toy> findByStockGreaterThan(Integer stock);
    List<Toy> findByCategoryAndStockGreaterThan(String category, Integer stock);
}

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);
    Optional<Cart> findByUserIdAndToyId(Long userId, Long toyId);
    void deleteByUserId(Long userId);
    boolean existsByUserIdAndToyId(Long userId, Long toyId);
}
```

## Клиентская часть — ViewModel

```kotlin
class ToyViewModel(application: Application) : AndroidViewModel(application) {

    /** Загружает все игрушки из локальной БД, обновляет StateFlow */
    fun loadToys()

    /**
     * Поиск игрушек по названию.
     * @param query — поисковый запрос
     */
    fun searchToys(query: String)

    /**
     * Фильтрация по категории.
     * @param category — название категории
     */
    fun filterByCategory(category: String)
}

class CartViewModel(application: Application) : AndroidViewModel(application) {

    /** Загружает корзину пользователя */
    fun loadCart()

    /**
     * Добавление товара в корзину.
     * @param toyId — идентификатор товара
     * @param quantity — количество
     */
    fun addToCart(toyId: Long, quantity: Int)

    /** Удаление позиции из корзины */
    fun removeFromCart(cartItemId: Long)

    /** Полная очистка корзины */
    fun clearCart()
}
```
```

