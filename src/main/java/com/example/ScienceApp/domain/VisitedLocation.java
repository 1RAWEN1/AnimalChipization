package com.example.ScienceApp.domain;


import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;

@Entity
public class VisitedLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive
    private Long visitedLocationId;

    @Basic
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date visitDate;

    public VisitedLocation(){}

    public VisitedLocation(Long id, Long visitedLocationId, Date visitDate) {
        this.id = id;
        this.visitedLocationId = visitedLocationId;
        this.visitDate = visitDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVisitedLocationId() {
        return visitedLocationId;
    }

    public void setVisitedLocationId(Long visitedLocationId) {
        this.visitedLocationId = visitedLocationId;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }
}
