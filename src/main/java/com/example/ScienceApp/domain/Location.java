package com.example.ScienceApp.domain;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "LOC_BASE")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(value = -90)
    @Max(value = 90)
    private double latitude;

    @NotNull
    @Min(value = -180)
    @Max(value = 180)
    private double longitude;

    public Location(){}

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
