package com.example.ScienceApp.controller;

import com.example.ScienceApp.domain.Animal;
import com.example.ScienceApp.domain.Type;
import com.example.ScienceApp.repos.AnimalRepo;
import com.example.ScienceApp.repos.TypeRepo;
import com.example.ScienceApp.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class TypeController {
    @Autowired
    TypeRepo typeRepo;

    @Autowired
    UserRepo userRepo;

    //Добавление нового типа
    @PostMapping("/animals/types")
    public Type addType(@RequestBody @Valid Type typeObj, BindingResult bindingResult){
        if(bindingResult.hasErrors() || typeObj == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if(typeRepo.findByType(typeObj.getType()) != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        else {
            return typeRepo.save(typeObj);
        }
    }

    //Получение типа по ИД
    @GetMapping("/animals/types/{type}")
    public Type typePage(@PathVariable Long type){
        if(type == null || type <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(typeRepo.findById(type).isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else {
            return typeRepo.findById(type).get();
        }
    }

    //Редактирование типа по ИД
    @PutMapping("/animals/types/{type}")
    public Type editType(@PathVariable(name = "type") Long id, @RequestBody @Valid Type type, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(typeRepo.findById(id).isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else if(typeRepo.findByType(type.getType()) != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        else {
            type.setId(id);
            typeRepo.save(type);
            return type;
        }
    }

    //Удаление типа по ИД
    @DeleteMapping("/animals/types/{type}")
    public void deleteType(@PathVariable(name = "type") Long id){
        if(id == null || id <= 0 || typeRepo.findById(id).isEmpty() || animalRepo.findAll().stream().anyMatch(animal -> animal.getTypes().stream().anyMatch(type -> type.equals(id)))){
            if(id != null && typeRepo.findById(id).isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else {
            typeRepo.deleteById(id);
        }
    }

    @Autowired
    AnimalRepo animalRepo;

    //Изменение типа у животного
    @PutMapping("/animals/{animal}/types")
    public Animal changeAnimalType(@PathVariable(name = "animal") Long id, @RequestParam Long oldTypeId, @RequestParam Long newTypeId){
        if(id == null || id <= 0 || oldTypeId <= 0 || newTypeId <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(animalRepo.findById(id).isEmpty() || typeRepo.findById((long) newTypeId).isEmpty()
                || typeRepo.findById((long) oldTypeId).isEmpty() || animalRepo.findById(id).get().getTypes().stream().noneMatch(type -> type.equals(oldTypeId))){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else if(animalRepo.findById(id).get().getTypes().stream().anyMatch(type -> type.equals(newTypeId))){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        else {
            Animal animal = animalRepo.findById(id).get();
            animal.setTypes(animal.getTypes().stream().filter(type -> !type.equals(oldTypeId)).collect(Collectors.toList()));
            typeRepo.findById(newTypeId).ifPresent(type -> animal.getTypes().add(type.getId()));

            return animalRepo.save(animal);
        }
    }

    //Добавление типа к животному
    @PostMapping("/animals/{animal}/types/{type}")
    public Animal addAnimalType(@PathVariable Long animal, @PathVariable Long type){
        if(animal == null || animal <= 0 || type == null || type <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(animalRepo.findById(animal).isEmpty() || typeRepo.findById(type).isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else if(animalRepo.findById(animal).get().getTypes().contains(type)){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        else {
            animalRepo.findById(animal).get().getTypes().add(type);
            return animalRepo.findById(animal).get();
        }
    }

    //Удаление типа у животного
    @DeleteMapping("/animals/{animalId}/types/{typeId}")
    public Animal deleteAnimalType(@PathVariable Long animalId, @PathVariable Long typeId){
        if(animalId == null || animalId <= 0 || typeId == null || typeId <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(animalRepo.findById(animalId).isEmpty() || typeRepo.findById(typeId).isEmpty() || !animalRepo.findById(animalId).get().getTypes().contains(typeId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else {
            Animal animal = animalRepo.findById(animalId).get();
            Type type = typeRepo.findById(typeId).get();
            animal.setTypes(animal.getTypes().stream().filter(type1 -> !Objects.equals(type1, type.getId()))
                    .collect(Collectors.toList()));

            return animalRepo.save(animal);
        }
    }
}
