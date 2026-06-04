# Реализация сервисного слоя (Mediator)

Сервисы содержат бизнес-логику и транзакционные границы. Управляют правами доступа (кто владелец данных) и оркестрируют операции с репозиториями.

## UserService

```java
@Service
@Transactional
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public UserResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Логин занят: " + request.username());
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Override
    public JwtResponse authenticate(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new AuthenticationException("Неверный логин или пароль"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AuthenticationException("Неверный логин или пароль");
        }
        String token = jwtService.generateToken(user);
        return new JwtResponse(token, "Bearer",
            user.getId(), user.getUsername(), user.getRole().name());
    }
}
```

## ToyService

```java
@Service
@Transactional
public class ToyService implements IToyService {

    private final ToyRepository toyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ToyResponse> getAllToys(String category) {
        List<Toy> toys = category != null
            ? toyRepository.findByCategory(category)
            : toyRepository.findAll();
        return toys.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ToyResponse getToyById(Long id) {
        Toy toy = toyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Игрушка не найдена: " + id));
        return toResponse(toy);
    }

    @Override
    public ToyResponse createToy(CreateToyRequest request) {
        Toy toy = new Toy();
        toy.setName(request.name());
        toy.setDescription(request.description());
        toy.setPrice(request.price());
        toy.setCategory(request.category());
        toy.setImageUrl(request.imageUrl());
        toy.setStock(request.stock());
        return toResponse(toyRepository.save(toy));
    }

    @Override
    public ToyResponse updateToy(Long id, UpdateToyRequest request) {
        Toy toy = toyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Игрушка не найдена: " + id));

        if (request.name() != null) toy.setName(request.name());
        if (request.description() != null) toy.setDescription(request.description());
        if (request.price() != null) toy.setPrice(request.price());
        if (request.category() != null) toy.setCategory(request.category());
        if (request.stock() != null) toy.setStock(request.stock());

        return toResponse(toyRepository.save(toy));
    }

    @Override
    public void deleteToy(Long id) {
        if (!toyRepository.existsById(id)) {
            throw new EntityNotFoundException("Игрушка не найдена: " + id);
        }
        toyRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ToyResponse> searchToys(String query) {
        return toyRepository.findByNameContainingIgnoreCase(query)
            .stream().map(this::toResponse).toList();
    }
}
```

## CartService

```java
@Service
@Transactional
public class CartService implements ICartService {

    private final CartRepository cartRepository;
    private final ToyRepository toyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CartResponse> getCartByUser(Long userId) {
        return cartRepository.findByUserId(userId)
            .stream().map(this::toResponse).toList();
    }

    @Override
    public CartResponse addToCart(Long userId, Long toyId, Integer quantity) {
        Toy toy = toyRepository.findById(toyId)
            .orElseThrow(() -> new EntityNotFoundException("Игрушка не найдена: " + toyId));

        if (toy.getStock() < quantity) {
            throw new InsufficientStockException(
                "Недостаточно товара на складе. Доступно: " + toy.getStock());
        }

        Optional<Cart> existing = cartRepository.findByUserIdAndToyId(userId, toyId);
        Cart cart;
        if (existing.isPresent()) {
            cart = existing.get();
            cart.setQuantity(cart.getQuantity() + quantity);
        } else {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setToyId(toyId);
            cart.setQuantity(quantity);
        }
        return toResponse(cartRepository.save(cart));
    }

    @Override
    public void removeFromCart(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findById(cartItemId)
            .orElseThrow(() -> new EntityNotFoundException("Позиция корзины не найдена"));
        if (!cart.getUserId().equals(userId)) {
            throw new AccessDeniedException("Нет доступа к этой позиции корзины");
        }
        cartRepository.delete(cart);
    }

    @Override
    public void clearCart(Long userId) {
        cartRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotal(Long userId) {
        List<Cart> items = cartRepository.findByUserId(userId);
        return items.stream()
            .map(item -> {
                Toy toy = toyRepository.findById(item.getToyId())
                    .orElseThrow(() -> new EntityNotFoundException("Товар не найден"));
                return toy.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

## JwtService

```java
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMs;

    public String generateToken(User user) {
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("userId", user.getId())
            .claim("role", user.getRole().name())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(getSignKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSignKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

