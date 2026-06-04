package com.toystore.controller;

import com.toystore.dto.CartResponse;
import com.toystore.model.User;
import com.toystore.repository.UserRepository;
import com.toystore.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "API for shopping cart management")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get user's cart")
    public ResponseEntity<List<CartResponse>> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("🔍 getCart: userDetails = " + userDetails);

        User user = loadFullUser(userDetails);
        System.out.println("🔍 getCart: user.id = " + user.getId());

        List<CartResponse> carts = cartService.getCartByUser(user);
        System.out.println("🔍 getCart: found " + carts.size() + " items");

        return ResponseEntity.ok(carts);
    }

    @PostMapping("/add")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<CartResponse> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long toyId,
            @RequestParam Integer quantity
    ) {
        System.out.println("🔍 addToCart: toyId = " + toyId + ", quantity = " + quantity);

        User user = loadFullUser(userDetails);
        System.out.println("🔍 addToCart: user.id = " + user.getId());

        CartResponse result = cartService.addToCart(user, toyId, quantity);
        System.out.println("🔍 addToCart: saved cart.id = " + result.getId());

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long id) {
        System.out.println("🔍 removeFromCart: id = " + id);
        cartService.removeFromCart(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear cart")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = loadFullUser(userDetails);
        System.out.println("🔍 clearCart: user.id = " + user.getId());
        cartService.clearCart(user);
        return ResponseEntity.ok().build();
    }

    private User loadFullUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("User not authenticated");
        }
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername()));
    }
}