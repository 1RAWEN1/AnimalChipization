package com.example.ScienceApp.repos;

import com.example.ScienceApp.domain.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepo extends JpaRepository<Type, Long> {
    Type findByType(String type);
}
