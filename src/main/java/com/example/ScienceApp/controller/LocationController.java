package com.example.ScienceApp.controller;

import com.example.ScienceApp.domain.Animal;
import com.example.ScienceApp.domain.Location;
import com.example.ScienceApp.domain.VisitedLocation;
import com.example.ScienceApp.repos.AnimalRepo;
import com.example.ScienceApp.repos.LocationRepo;
import com.example.ScienceApp.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class LocationController {
    @Autowired
    LocationRepo locRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    AnimalRepo animalRepo;

    //Добавление новой локации
    @PostMapping("/locations")
    public Location addLoc(@RequestBody Location loc, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(locRepo.findByLatitude(loc.getLatitude()).stream().anyMatch(loc1 -> loc1.getLongitude() == loc.getLongitude())){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        else {
            return locRepo.save(loc);
        }
    }

    //Получение локации по ИД
    @GetMapping("/locations/{loc}")
    public Location locPage(@PathVariable Long loc){
        if(loc == null || loc <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(locRepo.findById(loc).isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else {
            return locRepo.findById(loc).get();
        }
    }

    //Редактирование локации по ИД
    @PutMapping("/locations/{loc}")
    public Location editLoc(@PathVariable(name = "loc") Long id, @RequestBody Location location, BindingResult bindingResult){
        if(id == null || id <= 0 || bindingResult.hasErrors()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(locRepo.findById(id).isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else if(locRepo.findByLatitude(location.getLatitude()).stream().anyMatch(loc1 -> loc1.getLongitude() == location.getLongitude())){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        else {
            location.setId(id);
            return locRepo.save(location);
        }
    }

    //Удаление локации по ИД
    @DeleteMapping("/locations/{loc}")
    public void deleteLoc(@PathVariable(name = "loc") Long id){
        if(id == null || id <= 0
                || locRepo.findById(id).isPresent() && animalRepo.findAll().stream().noneMatch(animal -> animal.getVisitedLocations().stream().anyMatch(visitedLocation -> visitedLocation.getVisitedLocationId().equals(id)))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(locRepo.findById(id).isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else {
            locRepo.deleteById(id);
        }
    }

    //Поиск посещенных животным локаций по параметрам
    @GetMapping(value = "/animals/{id}/locations")
    public List<VisitedLocation> animalLocations(@PathVariable Long id, @RequestParam(name = "startDateTime") @DateTimeFormat(pattern="yyyy-MM-dd") Date startDateTime, @RequestParam(name = "endDateTime") @DateTimeFormat(pattern="yyyy-MM-dd") Date endDateTime, @RequestParam int size, @RequestParam int from){
        if(id == null || id <= 0 || from < 0 || size <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(animalRepo.findById(id).isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else {
            Animal animal = animalRepo.findById(id).get();

            return animal.getVisitedLocations().stream().filter(visitedLocation -> visitedLocation.getVisitDate().after(startDateTime) &&
                    visitedLocation.getVisitDate().before(endDateTime)).skip(from).limit(size).collect(Collectors.toList());
        }
    }

    //Изменение почещенной локации у животного
    @PutMapping("/animals/{id}/locations")
    public VisitedLocation changeAnimalLoc(@PathVariable Long id, @RequestParam Long visitedLocationPointId, @RequestParam Long locationPointId){
        if(id == null || id <= 0 || visitedLocationPointId <= 0 || locationPointId <= 0 || locationPointId.equals(visitedLocationPointId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(animalRepo.findById(id).isEmpty() || locRepo.findById(locationPointId).isEmpty() || locRepo.findById(visitedLocationPointId).isEmpty() ||
        animalRepo.findById(id).get().getVisitedLocations().stream().noneMatch(loc -> loc.getVisitedLocationId().equals(visitedLocationPointId))){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Animal animal = animalRepo.findById(id).get();
        if(animal.getVisitedLocations().stream().anyMatch(loc -> loc.getVisitedLocationId().equals(locationPointId)) || animal.getChippingLocationId().equals(locationPointId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else {
            VisitedLocation visitedLoc = new VisitedLocation();
            animal.setVisitedLocations(animal.getVisitedLocations().stream().filter(loc -> !loc.getVisitedLocationId().equals(visitedLocationPointId)).collect(Collectors.toList()));
            visitedLoc.setVisitedLocationId(locationPointId);
            visitedLoc.setVisitDate(new Date());

            return visitedLoc;
        }
    }

    //Добавление локации к животному
    @PostMapping("/animals/{animal1}/locations/{loc1}")
    public VisitedLocation addAnimalLoc(@PathVariable Long animal1, @PathVariable Long loc1){
        if(animal1 == null || animal1 <= 0 || loc1 == null || loc1 <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(animalRepo.findById(animal1).isEmpty() || locRepo.findById(loc1).isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Animal animal = animalRepo.findById(animal1).get();

        if(animal.getLifeStatus().equals("DEAD") || animal.getChippingLocationId().equals(loc1) || animal.getVisitedLocations().stream().anyMatch(loc2 -> loc2.getVisitedLocationId().equals(loc1))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else {
            VisitedLocation visitedLoc = new VisitedLocation();
            visitedLoc.setVisitedLocationId(loc1);
            visitedLoc.setVisitDate(new Date());
            List<VisitedLocation> animalVisitedLoc = new ArrayList<>(animal.getVisitedLocations());
            animalVisitedLoc.add(visitedLoc);
            animal.setVisitedLocations(animalVisitedLoc);

            return visitedLoc;
        }
    }

    //Удаление посещенной локации у животного
    @DeleteMapping("/animals/{animal1}/locations/{loc}")
    public void deleteAnimalLoc(@PathVariable Long animal1, @PathVariable Long loc){
        if(animal1 == null || animal1 <= 0 || loc == null || loc <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(animalRepo.findById(animal1).isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Animal animal = animalRepo.findById(animal1).get();

        if(animal.getVisitedLocations().stream().noneMatch(loc1 -> loc1.getVisitedLocationId().equals(loc))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else{
            animal.setVisitedLocations(animal.getVisitedLocations().stream().filter(loc1 -> !Objects.equals(loc1.getVisitedLocationId(), loc))
                    .collect(Collectors.toList()));
            animalRepo.save(animal);
        }
    }
}
