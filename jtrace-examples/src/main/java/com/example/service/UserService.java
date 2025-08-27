package com.example.service;

import com.example.domain.User;
import com.example.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * User service with intentional annotation violation.
 * Service methods should be annotated with @Transactional.
 */
@ApplicationScoped
public class UserService {
    
    @Inject
    private UserRepository userRepository;
    
    public List<User> getAllUsers() { // VIOLATION: Missing @Transactional
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) { // VIOLATION: Missing @Transactional
        return userRepository.findById(id);
    }
    
    public User createUser(User user) { // VIOLATION: Missing @Transactional
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) { // VIOLATION: Missing @Transactional
        userRepository.deleteById(id);
    }
}
