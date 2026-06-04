package com.toystore.service;

import com.toystore.model.Toy;
import com.toystore.repository.ToyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToyServiceTest {

    @Mock
    private ToyRepository toyRepository;

    @InjectMocks
    private ToyService toyService;

    private Toy toy;

    @BeforeEach
    void setUp() {
        toy = new Toy();
        toy.setId(1L);
        toy.setName("Test Toy");
        toy.setDescription("Test Description");
        toy.setPrice(BigDecimal.valueOf(29.99));
        toy.setStock(10);
        toy.setCategory("Action Figures");
    }

    @Test
    void getAllToys_ShouldReturnListOfToys() {
        when(toyRepository.findAll()).thenReturn(Arrays.asList(toy));

        List<Toy> result = toyService.getAllToys();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Toy", result.get(0).getName());
        verify(toyRepository, times(1)).findAll();
    }

    @Test
    void getToyById_WhenExists_ShouldReturnToy() {
        when(toyRepository.findById(1L)).thenReturn(Optional.of(toy));

        Toy result = toyService.getToyById(1L);

        assertNotNull(result);
        assertEquals("Test Toy", result.getName());
    }

    @Test
    void getToyById_WhenNotExists_ShouldThrowException() {
        when(toyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> toyService.getToyById(99L));
    }

    @Test
    void createToy_ShouldSaveAndReturn() {
        when(toyRepository.save(any(Toy.class))).thenReturn(toy);

        Toy result = toyService.createToy(toy);

        assertNotNull(result);
        verify(toyRepository, times(1)).save(toy);
    }

    @Test
    void deleteToy_ShouldDelete() {
        doNothing().when(toyRepository).deleteById(1L);

        toyService.deleteToy(1L);

        verify(toyRepository, times(1)).deleteById(1L);
    }
}