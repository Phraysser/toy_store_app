package com.toystore.repository;

import com.toystore.model.Toy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
// 👇 ДОБАВЬ ЭТУ СТРОКУ:
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ToyRepositoryTest {

    @Autowired
    private ToyRepository toyRepository;

    private Toy testToy;

    @BeforeEach
    void setUp() {
        testToy = new Toy();
        testToy.setName("Test Toy JPA");
        testToy.setDescription("Test Description");
        testToy.setPrice(BigDecimal.valueOf(29.99));
        testToy.setStock(10);
        testToy.setCategory("Action Figures");
    }

    @Test
    void save_ShouldCreateToy() {
        Toy savedToy = toyRepository.save(testToy);
        assertNotNull(savedToy.getId());
        assertEquals("Test Toy JPA", savedToy.getName());
    }

    @Test
    void findById_ShouldReturnToy() {
        Toy savedToy = toyRepository.save(testToy);
        Optional<Toy> foundToy = toyRepository.findById(savedToy.getId());
        assertTrue(foundToy.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllToys() {
        toyRepository.save(testToy);
        Toy secondToy = new Toy();
        secondToy.setName("Second Toy JPA");
        secondToy.setPrice(BigDecimal.valueOf(19.99));
        secondToy.setStock(5);
        toyRepository.save(secondToy);

        List<Toy> toys = toyRepository.findAll();
        assertTrue(toys.size() > 0);
    }
}