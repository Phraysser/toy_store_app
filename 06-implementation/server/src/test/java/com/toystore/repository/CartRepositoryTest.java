package com.toystore.repository;

import com.toystore.model.Cart;
import com.toystore.model.Toy;
import com.toystore.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ToyRepository toyRepository;

    private User testUser;
    private Toy testToy;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("cartUserTest");
        testUser.setPassword("password");
        testUser.setRole(User.Role.USER);
        userRepository.save(testUser);

        testToy = new Toy();
        testToy.setName("Cart Toy Test");
        testToy.setPrice(BigDecimal.valueOf(50.00));
        testToy.setStock(10);
        toyRepository.save(testToy);
    }

    @Test
    void save_ShouldCreateCartItem() {
        Cart cart = new Cart();
        cart.setUser(testUser);
        cart.setToy(testToy);
        cart.setQuantity(2);

        Cart savedCart = cartRepository.save(cart);

        assertNotNull(savedCart.getId());
        assertEquals(2, savedCart.getQuantity());
    }

    @Test
    void findByUser_ShouldReturnCartItems() {
        Cart cart1 = new Cart();
        cart1.setUser(testUser);
        cart1.setToy(testToy);
        cart1.setQuantity(1);
        cartRepository.save(cart1);

        Cart cart2 = new Cart();
        cart2.setUser(testUser);
        cart2.setToy(testToy);
        cart2.setQuantity(3);
        cartRepository.save(cart2);

        List<Cart> userCarts = cartRepository.findByUser(testUser);
        assertEquals(2, userCarts.size());
    }

    @Test
    void deleteByUser_ShouldRemoveAllItems() {
        Cart cart = new Cart();
        cart.setUser(testUser);
        cart.setToy(testToy);
        cart.setQuantity(1);
        cartRepository.save(cart);

        cartRepository.deleteByUser(testUser);

        List<Cart> userCarts = cartRepository.findByUser(testUser);
        assertTrue(userCarts.isEmpty());
    }
}