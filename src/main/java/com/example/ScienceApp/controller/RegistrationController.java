package com.example.ScienceApp.controller;

import com.example.ScienceApp.domain.User;
import com.example.ScienceApp.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
public class RegistrationController {
    @Autowired
    private UserRepo userRepo;

    public static User authUser;

    //Регистрация нового пользователя
    @PostMapping("/registration")
    public void addUser(@RequestBody @Valid User user, BindingResult bindingResult){
        if(user == null || bindingResult.hasErrors()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if(userRepo.findByEmail(user.getEmail()).size() != 0){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        else if(RegistrationController.authUser != null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        else{
            userRepo.save(user);
        }
    }

    //Авторизация пользователя на сайте
    @PostMapping("/login")
    public void singIn(@RequestBody @Valid User user){
        authUser = null;
        if(user != null && userRepo.findByEmail(user.getEmail()).size() > 0 && userRepo.findByEmail(user.getEmail()).get(0).getPassword().equals(user.getPassword())){
            authUser = userRepo.findByEmail(user.getEmail()).get(0);
        }
        else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
