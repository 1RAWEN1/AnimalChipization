package com.example.ScienceApp.repos;

import com.example.ScienceApp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface UserRepo extends JpaRepository<User, Long> {
    List<User> findByEmail(String email);

    User findByUsername(String username);
}
