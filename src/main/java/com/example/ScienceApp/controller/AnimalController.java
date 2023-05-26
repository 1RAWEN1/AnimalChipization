package com.example.ScienceApp.controller;

import com.example.ScienceApp.domain.Animal;
import com.example.ScienceApp.repos.AnimalRepo;
import com.example.ScienceApp.repos.LocationRepo;
import com.example.ScienceApp.repos.TypeRepo;
import com.example.ScienceApp.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AnimalController {
    @Autowired
    AnimalRepo animalRepo;

    @Autowired
    UserRepo userRepo;
    @Autowired
    LocationRepo locRepo;

    //Получение животного по ИД
    @GetMapping("/animals/{id}")
    public Animal animalInfo(@PathVariable Long id){
        if(id == null || id <= 0){
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
            return animalRepo.findById(id).get();
        }
    }

    //Редактирование животного по ИД
    @PutMapping("/animals/{animal}")
    public Animal editAnimal(@PathVariable(name = "animal") Long animal, @RequestBody @Valid Animal animal1, BindingResult bindingResult){
        if(animal == null || animal <= 0 || !animal1.getLifeStatus().equals("ALIVE") &&
                !animal1.getLifeStatus().equals("DEAD") || !animal1.getGender().equals("MALE") &&
                !animal1.getGender().equals("FEMALE") && !animal1.getGender().equals("OTHER") ||
                bindingResult.hasErrors()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(animalRepo.findById(animal).isEmpty() || userRepo.findById((animalRepo.findById(animal).get().getChipperId())).isEmpty() ||
        locRepo.findById(animalRepo.findById(animal).get().getChippingLocationId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else {
            Animal animal2 = animalRepo.findById(animal).get();
            if(animal2.getLifeStatus().equals("DEAD") && animal1.getLifeStatus().equals("ALIVE")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            else {
                animal1.setId(animal);

                return animalRepo.save(animal1);
            }
        }
    }

    //Удаление животного по ИД
    @DeleteMapping("/animals/{animal}")
    public void deleteAnimal(@PathVariable(name = "animal") Long id){
        if(id == null || id <= 0){
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
            animalRepo.deleteById(id);
        }
    }

    //Поиск животного по параметрам
    @GetMapping("/animals/search")
    public List<Animal> searchAnimal(@RequestParam String lifeStatus, @RequestParam String gender, @RequestParam Long chipperId,
                                     @RequestParam Long chippingLocationId, @RequestParam(name = "startDateTime") @DateTimeFormat(pattern="yyyy-MM-dd") Date startDateTime,
                                     @RequestParam(name = "endDateTime") @DateTimeFormat(pattern="yyyy-MM-dd") Date endDateTime, @RequestParam int size, @RequestParam int from){
        if(size <= 0 || from < 0 || chipperId <= 0 || chippingLocationId <= 0 ||
        !lifeStatus.equals("ALIVE") && !lifeStatus.equals("DEAD") || !gender.equals("MALE") && !gender.equals("FEMALE")
        && !gender.equals("OTHER")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else {
            List<Animal> animals = animalRepo.findByLifeStatus(lifeStatus);
            animals = animals.stream().filter(animal1 -> animal1.getGender().equals(gender)).collect(Collectors.toList());
            animals = animals.stream().filter(animal1 -> animal1.getChipperId() != null ? Objects.equals(animal1.getChipperId(), chipperId) :
                            animal1.getChippingLocationId() != null ? Objects.equals(animal1.getChippingLocationId(), chippingLocationId) :
                                    startDateTime != null ? startDateTime.before(animal1.getChippingDateTime()) :
                                            endDateTime == null || endDateTime.after(animal1.getChippingDateTime())).skip(from).limit(size).collect(Collectors.toList());

            return animals;
        }
    }

    @Autowired
    TypeRepo typeRepo;

    //Добавление животного
    @PostMapping("/animals")
    public Animal addAnimal(@RequestBody Animal animal, BindingResult bindingResult){
        if(animal.getTypes().size() <= 0 || !animal.getGender().equals("MALE") && !animal.getGender().equals("FEMALE") &&
                !animal.getGender().equals("OTHER") || bindingResult.hasErrors()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(animal.getTypes().stream().anyMatch(typeId -> typeRepo.findById(typeId).isEmpty()) ||
        userRepo.findById(animal.getChipperId()).isEmpty() || locRepo.findById(animal.getChippingLocationId()).isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else {
            return animalRepo.save(animal);
        }
    }
}
