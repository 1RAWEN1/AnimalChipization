package com.example.ScienceApp.domain;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.*;

@Entity
@Table(name = "ANIMAL_BASE")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive
    @JoinColumn(name = "location_id")
    private Long chippingLocationId;

    @NotNull
    @ElementCollection
    @CollectionTable(name="animal_types", joinColumns = @JoinColumn(name = "type_id"))
    private List<Long> types = new ArrayList<>();

    @NotNull
    @OneToMany
    @CollectionTable(name="animal_visited_locations", joinColumns = @JoinColumn(name = "visit_id"))
    @Valid
    private List<VisitedLocation> visitedLocations = new ArrayList<>();

    @NotNull
    @Positive
    private float weight;
    @NotNull
    @Positive
    private float length;
    @NotNull
    @Positive
    private float height;
    @NotNull
    private String gender;
    @NotNull
    private String lifeStatus;
    @Basic
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date chippingDateTime;
    @Basic
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deathDateTime;

    @NotNull
    @Positive
    @JoinColumn(name = "chipper_id")
    private Long chipperId;

    public Animal(){}

    public Animal(Long chippingLocationId, List<Long> types, List<VisitedLocation> visitedLocations, float weight, float length, float height, String gender, Date chippingDateTime, Long chipperId) {
        this.chippingLocationId = chippingLocationId;
        this.types = types;
        this.visitedLocations = visitedLocations;
        this.weight = weight;
        this.length = length;
        this.height = height;
        this.gender = gender;
        this.lifeStatus = "ALIVE";
        this.chippingDateTime = chippingDateTime;
        this.chipperId = chipperId;
    }

    public Long getChippingLocationId() {
        return chippingLocationId;
    }

    public void setChippingLocationId(Long chippingLocationId) {
        this.chippingLocationId = chippingLocationId;
    }

    public List<Long> getTypes() {
        return types;
    }

    public void setTypes(List<Long> types) {
        this.types = types;
    }

    public List<VisitedLocation> getVisitedLocations() {
        return visitedLocations;
    }

    public void setVisitedLocations(List<VisitedLocation> visitedLocations) {
        this.visitedLocations = visitedLocations;
    }

    public Long getChipperId() {
        return chipperId;
    }

    public void setChipperId(Long chipperId) {
        chippingDateTime = new Date();
        this.chipperId = chipperId;
    }

    public String getLifeStatus() {
        return lifeStatus;
    }

    public void setLifeStatus(String lifeStatus) {
        if(this.lifeStatus == null && lifeStatus.equals("DEAD")){
            deathDateTime = Calendar.getInstance().getTime();
        }
        else if(lifeStatus.equals("DEAD") && !this.lifeStatus.equals(lifeStatus)){
            deathDateTime = Calendar.getInstance().getTime();
        }
        this.lifeStatus = lifeStatus;
    }

    public Date getChippingDateTime() {
        return chippingDateTime;
    }

    public void setChippingDateTime(Date chippingDateTime) {
        this.chippingDateTime = chippingDateTime;
    }

    public Date getDeathDateTime() {
        return deathDateTime;
    }

    public void setDeathDateTime(Date deathDateTime) {
        this.deathDateTime = deathDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
