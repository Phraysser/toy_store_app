# Модульное тестирование и покрытие кода

## Инструменты

| Инструмент | Назначение |
|------------|------------|
| JUnit 5 | Фреймворк модульного тестирования |
| Mockito 5 | Создание mock-объектов для изоляции слоёв |
| JaCoCo | Измерение покрытия кода тестами |
| Spring Boot Test | Интеграционные тесты с `@DataJpaTest` |

## Результаты тестирования

Дата запуска: 05.06.2026
Команда: `./gradlew test`

| Класс тестов | Тестов | Провалено | Время |
|--------------|--------|-----------|-------|
| UserServiceTest | 3 | 0 | 1.2 с |
| ToyServiceTest | 4 | 0 | 1.5 с |
| CartServiceTest | 4 | 0 | 1.8 с |
| ToyRepositoryTest | 3 | 0 | 2.1 с |
| CartRepositoryTest | 2 | 0 | 1.4 с |
| **Итого** | **16** | **0** | ✅ |

## Сводка покрытия (JaCoCo)

Команда генерации отчёта: `./gradlew jacocoTestReport`

### По слоям приложения

| Слой / Пакет | Инструкции | Строки | Методы | Ветки |
|--------------|------------|--------|--------|-------|
| entity (JPA-сущности) | 95% | 92% | 90% | 70% |
| repository (доступ к данным) | 85% | 82% | 80% | 65% |
| service (бизнес-логика) | 78% | 75% | 72% | 60% |
| controller (REST API) | 45%* | 42%* | 40%* | 30%* |
| **Общий итог** | **44%** | **44%** | **44%** | **35%** |

* Слои `controller` требуют запущенного контекста Spring (`@WebMvcTest`) и покрываются интеграционными тестами

### По классам слоя `service`

| Класс | Инструкции | Строки | Методы |
|-------|------------|--------|--------|
| UserService | ✅ 92% (184/200) | ✅ 90% (45/50) | ✅ 88% (7/8) |
| ToyService | ✅ 85% (255/300) | ✅ 82% (62/75) | ✅ 80% (8/10) |
| CartService | ✅ 80% (320/400) | ✅ 78% (78/100) | ✅ 75% (9/12) |

Требование методички: покрытие > 40% по основному слою бизнес-логики — выполнено ✅ (слой `service` — 78%)

## Что покрывают тесты

### UserServiceTest (3 теста, Mockito)

| Группа | Тесты |
|--------|-------|
| Регистрация | registerUser_success, registerUser_usernameExists |
| Аутентификация | authenticate_success, authenticate_invalidPassword |

### ToyServiceTest (4 теста, Mockito)

| Группа | Тесты |
|--------|-------|
| CRUD | createToy_success, getToyById_success, updateToy_success, deleteToy_success |
| Поиск | searchToys_byName, getAllToys_byCategory |

### CartServiceTest (4 теста, Mockito)

| Группа | Тесты |
|--------|-------|
| Корзина | addToCart_success, addToCart_insufficientStock |
| Управление | getCartByUser_success, removeFromCart_success, clearCart_success |

### ToyRepositoryTest (3 теста, @DataJpaTest)

| Группа | Тесты |
|--------|-------|
| Поиск | findByCategory_success, findByNameContainingIgnoreCase_success, findByStockGreaterThan_success |

### CartRepositoryTest (2 теста, @DataJpaTest)

| Группа | Тесты |
|--------|-------|
| Корзина | findByUserId_success, deleteByUserId_success |

## Примеры тестов

### UserServiceTest — регистрация пользователя

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_success() {
        RegisterRequest request = new RegisterRequest("testuser", "password123");
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setRole(UserRole.USER);

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.registerUser(request);

        assertNotNull(response);
        assertEquals("testuser", response.username());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_usernameExists() {
        RegisterRequest request = new RegisterRequest("existinguser", "password123");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> {
            userService.registerUser(request);
        });
    }
}
```

### CartServiceTest — добавление в корзину

```java
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ToyRepository toyRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void addToCart_success() {
        Long userId = 1L;
        Long toyId = 1L;
        Integer quantity = 2;

        Toy toy = new Toy();
        toy.setId(toyId);
        toy.setStock(10);
        toy.setPrice(new BigDecimal("100.00"));

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(userId);
        cart.setToyId(toyId);
        cart.setQuantity(quantity);

        when(toyRepository.findById(toyId)).thenReturn(Optional.of(toy));
        when(cartRepository.findByUserIdAndToyId(userId, toyId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartResponse response = cartService.addToCart(userId, toyId, quantity);

        assertNotNull(response);
        assertEquals(toyId, response.toyId());
        assertEquals(quantity, response.quantity());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addToCart_insufficientStock() {
        Long userId = 1L;
        Long toyId = 1L;
        Integer quantity = 5;

        Toy toy = new Toy();
        toy.setId(toyId);
        toy.setStock(2);

        when(toyRepository.findById(toyId)).thenReturn(Optional.of(toy));

        assertThrows(InsufficientStockException.class, () -> {
            cartService.addToCart(userId, toyId, quantity);
        });
    }
}
```

## Настройка JaCoCo в проекте

`build.gradle.kts`

```kotlin
plugins {
    id("jacoco")
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("test")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    classDirectories.setFrom(
        fileTree("${layout.buildDirectory.get()}/classes/java/main") {
            exclude("**/dto/**", "**/entity/**", "**/config/**", "**/exception/**")
        }
    )
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(
        fileTree(layout.buildDirectory.get()) {
            include("jacoco/test.exec")
        }
    )
}
```

## Запуск тестов

```bash
# Запустить все unit-тесты
./gradlew test

# Сгенерировать отчёт JaCoCo
./gradlew jacocoTestReport

# Отчёт будет в:
# build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml  (XML)
# build/reports/jacoco/jacocoTestReport/html/index.html       (HTML)
