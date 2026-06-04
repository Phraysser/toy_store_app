package com.toystore.service;

import com.toystore.dto.CartResponse;  // 👈 Импортируем DTO
import com.toystore.model.Cart;
import com.toystore.model.Toy;
import com.toystore.model.User;
import com.toystore.repository.CartRepository;
import com.toystore.repository.ToyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ToyRepository toyRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void getCartByUser_ShouldReturnCartResponses() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Toy toy = new Toy();
        toy.setId(1L);
        toy.setName("Test Toy");
        toy.setPrice(new BigDecimal("99.99"));

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setToy(toy);
        cart.setQuantity(2);

        when(cartRepository.findByUserWithToy(user)).thenReturn(List.of(cart));

        // Act
        List<CartResponse> result = cartService.getCartByUser(user);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Test Toy", result.get(0).getToyName());
        assertEquals(199.98, result.get(0).getTotal());  // 2 * 99.99
    }

    @Test
    void addToCart_ShouldReturnCartResponse() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Toy toy = new Toy();
        toy.setId(1L);
        toy.setName("Test Toy");
        toy.setPrice(new BigDecimal("99.99"));

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setToy(toy);
        cart.setQuantity(1);

        when(toyRepository.findById(1L)).thenReturn(Optional.of(toy));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        CartResponse result = cartService.addToCart(user, 1L, 1);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Toy", result.getToyName());
        assertEquals(99.99, result.getPrice());
    }

    @Test
    void removeFromCart_ShouldCallRepository() {
        // Act
        cartService.removeFromCart(1L);

        // Assert
        verify(cartRepository, times(1)).deleteById(1L);
    }

    @Test
    void clearCart_ShouldCallRepository() {
        // Arrange
        User user = new User();
        user.setId(1L);

        // Act
        cartService.clearCart(user);

        // Assert
        verify(cartRepository, times(1)).deleteByUser(user);
    }
}