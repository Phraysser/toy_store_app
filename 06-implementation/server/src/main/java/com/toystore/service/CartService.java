package com.toystore.service;

import com.toystore.dto.CartResponse;
import com.toystore.model.Cart;
import com.toystore.model.Toy;
import com.toystore.model.User;
import com.toystore.repository.CartRepository;
import com.toystore.repository.ToyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ToyRepository toyRepository;

    @Transactional(readOnly = true)
    public List<CartResponse> getCartByUser(User user) {
        List<Cart> carts = cartRepository.findByUserWithToy(user);
        return carts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CartResponse addToCart(User user, Long toyId, Integer quantity) {
        Toy toy = toyRepository.findById(toyId)
                .orElseThrow(() -> new RuntimeException("Toy not found"));

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setToy(toy);
        cart.setQuantity(quantity);

        Cart savedCart = cartRepository.save(cart);
        return toResponse(savedCart);
    }

    @Transactional
    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    @Transactional
    public void clearCart(User user) {
        cartRepository.deleteByUser(user);
    }


    private CartResponse toResponse(Cart cart) {
        Toy toy = cart.getToy();
        Integer quantity = cart.getQuantity();

        BigDecimal total = toy.getPrice().multiply(BigDecimal.valueOf(quantity));

        return new CartResponse(
                cart.getId(),
                cart.getUser().getId(),
                cart.getUser().getUsername(),
                toy.getId(),
                toy.getName(),
                toy.getImageUrl(),
                toy.getPrice().doubleValue(),
                quantity,
                total.doubleValue()
        );
    }
}