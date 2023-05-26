package com.example.ScienceApp.repos;

import com.example.ScienceApp.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepo extends JpaRepository<Location, Long> {
    List<Location> findByLatitude(double latitude);
}
