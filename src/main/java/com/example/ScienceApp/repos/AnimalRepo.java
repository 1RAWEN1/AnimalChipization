package com.example.ScienceApp.repos;

import com.example.ScienceApp.domain.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepo extends JpaRepository<Animal, Long> {
    List<Animal> findByLifeStatus(String lifeStatus);

    List<Animal> findByChipperId(Long chipperId);
}
