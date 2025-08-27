package com.example.repository;

import com.example.domain.User;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entities.
 */
public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(Long id);
    User save(User user);
    void deleteById(Long id);
}
