package com.hivecare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hivecare.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(String role);

    List<User> findByRoleAndWorkerService(
            String role,
            String workerService
    );
}