package com.example.ScienceApp.controller;

import com.example.ScienceApp.domain.User;
import com.example.ScienceApp.repos.AnimalRepo;
import com.example.ScienceApp.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class MainController {
    @Autowired
    UserRepo userRepo;

    //Получение пользователя по ИД
    @GetMapping("/accounts/{id}")
    public User userPage(@PathVariable Long id){
        if(id == null || id <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(userRepo.findById(id).isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else {
            return userRepo.findById(id).get();
        }
    }

    //Поиск пользователя по параметрам
    @GetMapping("/accounts/search")
    public List<User> searchAccount(@RequestParam String email, @RequestParam String firstName,@RequestParam String lastName,
                                    @RequestParam int size, @RequestParam int from){
        if(size <= 0 || from < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null ||
                userRepo.findByEmail(RegistrationController.authUser.getEmail()).size() == 0 || !userRepo.findByEmail(RegistrationController.authUser.getEmail()).get(0).getPassword().equals(RegistrationController.authUser.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else {
            List<User> users = !email.equals("") ? userRepo.findByEmail(email) :
                    userRepo.findAll();
            users = users.stream().filter(user1 -> !firstName.equals("") ? user1.getFirstName().equals(firstName)
                    : lastName.equals("") || user1.getLastName().equals(lastName)).skip(from).limit(size).collect(Collectors.toList());
            return users;
        }
    }

    //Редактирование аккаунта пользователя
    @PutMapping("/accounts/{user}")
    public User editUser(@PathVariable(name = "user") Long id, @RequestBody @Valid User user, BindingResult bindingResult){
        if(bindingResult.hasErrors() || id == null || id <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null || userRepo.findById(id).isPresent() && !userRepo.findById(id).get().getPassword().equals(user.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(!Objects.equals(RegistrationController.authUser.getId(), user.getId()) || userRepo.findById(id).isEmpty()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        else if(userRepo.findByEmail(user.getEmail()).size() != 0){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        else {
            return userRepo.save(user);
        }
    }

    @Autowired
    AnimalRepo animalRepo;

    //Удаление аккаунта пользователя
    @DeleteMapping("/accounts/{user}")
    public void deleteUser(@PathVariable(name = "user") Long id, @RequestBody @Valid User user){
        if(animalRepo.findByChipperId(user.getId()).size() != 0 ||
                id == null || id <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(RegistrationController.authUser == null || userRepo.findById(id).isPresent() && !userRepo.findById(id).get().getPassword().equals(user.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        else if(!Objects.equals(RegistrationController.authUser.getId(), user.getId()) || userRepo.findById(id).isEmpty()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        else {
            userRepo.deleteById(user.getId());
        }
    }
}
