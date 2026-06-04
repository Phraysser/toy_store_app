package com.toystore.repository;

import com.toystore.model.Toy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;  // 👈 Добавь этот импорт
import org.springframework.data.repository.query.Param;  // 👈 Добавь этот импорт
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ToyRepository extends JpaRepository<Toy, Long> {
    List<Toy> findByNameContainingIgnoreCase(String name);

    @Query("SELECT t FROM Toy t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.category) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Toy> searchByNameOrCategory(@Param("query") String query);
}