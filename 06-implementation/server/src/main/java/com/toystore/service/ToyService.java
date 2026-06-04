package com.toystore.service;

import com.toystore.model.Toy;
import com.toystore.repository.ToyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ToyService {

    private final ToyRepository toyRepository;

    public List<Toy> getAllToys() {
        return toyRepository.findAll();
    }

    public Toy getToyById(Long id) {
        return toyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Toy not found"));
    }

    public Toy createToy(Toy toy) {
        return toyRepository.save(toy);
    }

    public Toy updateToy(Long id, Toy toyDetails) {
        Toy toy = getToyById(id);
        toy.setName(toyDetails.getName());
        toy.setDescription(toyDetails.getDescription());
        toy.setPrice(toyDetails.getPrice());
        toy.setImageUrl(toyDetails.getImageUrl());
        toy.setStock(toyDetails.getStock());
        toy.setCategory(toyDetails.getCategory());
        return toyRepository.save(toy);
    }
    @Transactional
    public void deleteToy(Long id) {
        toyRepository.deleteById(id);
    }

    public List<Toy> searchToys(String query) {
        if (query == null || query.trim().isEmpty()) {
            return toyRepository.findAll();
        }
        return toyRepository.searchByNameOrCategory(query.trim());
    }
}