package com.toystore.repository;

import com.toystore.model.Cart;
import com.toystore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUser(User user);

    void deleteByUser(User user);


    @Query("SELECT c FROM Cart c JOIN FETCH c.toy WHERE c.user = :user")
    List<Cart> findByUserWithToy(@Param("user") User user);
}