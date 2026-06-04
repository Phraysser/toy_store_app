# Реализация Entity-слоя

Entity-классы представляют бизнес-объекты, отображённые на таблицы базы данных. Аннотация `@Entity` подключает JPA/Hibernate. Классы не являются «анемичными» — содержат бизнес-методы.

## User

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart> carts = new ArrayList<>();

    // Бизнес-метод: проверка роли
    public boolean isAdmin() {
        return UserRole.ADMIN.equals(this.role);
    }

    // Бизнес-метод: отображаемое имя
    public String getDisplayName() {
        return username;
    }
}

public enum UserRole { USER, ADMIN }
```

## Toy

```java
@Entity
@Table(name = "toys")
public class Toy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 255)
    private String category;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "toy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart> carts = new ArrayList<>();

    // Бизнес-метод: есть ли в наличии
    public boolean isInStock() {
        return stock != null && stock > 0;
    }

    // Бизнес-метод: добавить на склад
    public void addToStock(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Количество не может быть отрицательным");
        this.stock += amount;
    }

    // Бизнес-метод: зарезервировать со склада
    public void reserveFromStock(int amount) {
        if (amount > this.stock) {
            throw new InsufficientStockException("Недостаточно товара на складе");
        }
        this.stock -= amount;
    }
}
```

## Cart

```java
@Entity
@Table(name = "carts", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "toy_id"})
})
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "toy_id", nullable = false)
    private Long toyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toy_id", insertable = false, updatable = false)
    private Toy toy;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    // Бизнес-метод: увеличить количество
    public void increaseQuantity(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Количество должно быть положительным");
        this.quantity += amount;
    }

    // Бизнес-метод: расчёт стоимости позиции
    public BigDecimal getTotalPrice() {
        if (toy == null || toy.getPrice() == null) return BigDecimal.ZERO;
        return toy.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
```

## DTO (Data Transfer Objects)

### Запросы (Request)

```java
public record LoginRequest(
    @NotBlank String username,
    @NotBlank String password
) {}

public record RegisterRequest(
    @NotBlank @Size(min = 3, max = 50) String username,
    @NotBlank @Size(min = 6) String password
) {}

public record CreateToyRequest(
    @NotBlank @Size(max = 255) String name,
    String description,
    @NotNull @DecimalMin("0") BigDecimal price,
    @NotBlank String category,
    String imageUrl,
    @NotNull @Min(0) Integer stock
) {}

public record UpdateToyRequest(
    String name,
    String description,
    BigDecimal price,
    String category,
    String imageUrl,
    Integer stock
) {}

public record AddToCartRequest(
    @NotNull Long toyId,
    @NotNull @Min(1) Integer quantity
) {}
```

### Ответы (Response)

```java
public record UserResponse(
    Long id,
    String username,
    String role,
    LocalDateTime createdAt
) {}

public record JwtResponse(
    String token,
    String type,      // "Bearer"
    Long userId,
    String username,
    String role
) {}

public record ToyResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String category,
    String imageUrl,
    Integer stock,
    boolean inStock,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

public record CartResponse(
    Long id,
    Long userId,
    Long toyId,
    String toyName,
    String imageUrl,
    BigDecimal price,
    Integer quantity,
    BigDecimal total
) {}

