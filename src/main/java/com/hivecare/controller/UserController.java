package com.hivecare.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hivecare.model.User;
import com.hivecare.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (userRepository.existsByEmail(user.getEmail())) {

            return ResponseEntity.badRequest()
                    .body("Email already exists");
        }

        // Normal registration
        if (user.getRole() == null ||
            user.getRole().isBlank()) {

            user.setRole("USER");
        }

        userRepository.save(user);

        return ResponseEntity.ok("Registration Successful");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody User user) {

        User existingUser =
                userRepository.findByEmail(user.getEmail());

        if (existingUser != null &&
            existingUser.getPassword()
                    .equals(user.getPassword())) {

            return ResponseEntity.ok(existingUser);
        }

        return ResponseEntity.badRequest()
                .body("Invalid Email or Password");
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable Long id) {

        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User userDetails) {

        return userRepository.findById(id)
                .map(user -> {

                    user.setName(userDetails.getName());
                    user.setEmail(userDetails.getEmail());
                    user.setPhone(userDetails.getPhone());
                    user.setAddress(userDetails.getAddress());
                    user.setPassword(userDetails.getPassword());

                    return ResponseEntity.ok(
                            userRepository.save(user)
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }
}