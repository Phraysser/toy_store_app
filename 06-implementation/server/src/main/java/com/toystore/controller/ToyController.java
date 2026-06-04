package com.toystore.controller;

import com.toystore.model.Toy;
import com.toystore.service.ToyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/toys")
@RequiredArgsConstructor
@Tag(name = "Toys", description = "API for toy management")
public class ToyController {

    private final ToyService toyService;

    @GetMapping
    @Operation(summary = "Get all toys")
    public ResponseEntity<List<Toy>> getAllToys() {
        return ResponseEntity.ok(toyService.getAllToys());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get toy by ID")
    public ResponseEntity<Toy> getToyById(@PathVariable Long id) {
        return ResponseEntity.ok(toyService.getToyById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search toys by name or category")
    public ResponseEntity<List<Toy>> searchToys(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(toyService.searchToys(query));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new toy (Admin only)")
    public ResponseEntity<Toy> createToy(@RequestBody Toy toy) {
        return ResponseEntity.ok(toyService.createToy(toy));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update toy (Admin only)")
    public ResponseEntity<Toy> updateToy(@PathVariable Long id, @RequestBody Toy toy) {
        return ResponseEntity.ok(toyService.updateToy(id, toy));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete toy (Admin only)")
    public ResponseEntity<Void> deleteToy(@PathVariable Long id) {
        toyService.deleteToy(id);
        return ResponseEntity.ok().build();
    }
}