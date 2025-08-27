package com.example.controller;

import com.example.domain.User;
import com.example.repository.UserRepository; // VIOLATION: Controller should not import repository
import com.example.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * User controller with intentional dependency violation.
 * Controllers should not directly access repositories.
 */
@WebServlet("/users")
@ApplicationScoped
public class UserController extends HttpServlet {
    
    @Inject
    private UserService userService;
    
    @Inject
    private UserRepository userRepository; // VIOLATION: Controller should not inject repository
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        
        // VIOLATION: Controller directly calling repository instead of service
        List<User> users = userRepository.findAll();
        
        resp.setContentType("application/json");
        resp.getWriter().write("{\"users\": " + users.size() + "}");
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        
        // This is correct - using service
        User user = new User("test", "test@example.com");
        userService.createUser(user);
        
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }
}
