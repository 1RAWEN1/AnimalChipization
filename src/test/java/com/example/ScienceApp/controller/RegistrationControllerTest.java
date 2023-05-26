package com.example.ScienceApp.controller;

import com.example.ScienceApp.domain.User;
import com.example.ScienceApp.repos.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserRepo userRepo;

    User user = new User("John", "Doe", "12sadjg12", "jd@gmail.com");

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void addUser_success() throws Exception {
        RegistrationController.authUser = null;
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void addUser_emailError() throws Exception {
        Mockito.when(userRepo.findByEmail(user.getEmail())).thenReturn(List.of(user));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addUser_authError() throws Exception {
        RegistrationController.authUser = user;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addUser_bindingError() throws Exception {
        User user1 = new User("John", "Doe", "12sadjg12", "jd@gmail.com");
        user1.setPassword(" ");

        Set<ConstraintViolation<User>> constraintViolations =
                validator.validate(user1);

        assertEquals(1, constraintViolations.size());

        user1 = new User("John", "Doe", "12sadjg12", "jd@gmail.com");
        user1.setEmail("d21e");

        constraintViolations =
                validator.validate(user1);

        assertEquals(1, constraintViolations.size());

        user1 = new User("John", "Doe", "12sadjg12", "jd@gmail.com");
        user1.setFirstName(" ");

        constraintViolations =
                validator.validate(user1);

        assertEquals(1, constraintViolations.size());

        user1 = new User("John", "Doe", "12sadjg12", "jd@gmail.com");
        user1.setLastName(" ");

        constraintViolations =
                validator.validate(user1);

        assertEquals(1, constraintViolations.size());
    }

    @Test
    void singIn_success() throws Exception {
        user.setId(2L);
        Mockito.when(userRepo.findByEmail(any())).thenReturn(List.of(user));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void singIn_errorNotFound() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof ResponseStatusException));
    }

    @Test
    void singIn_errorWrongPassword() throws Exception {
        Mockito.when(userRepo.findByEmail(any())).thenReturn(List.of(user));

        User user1 = new User("John", "Doe", "12sadjg1", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user1));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof ResponseStatusException));
    }
}