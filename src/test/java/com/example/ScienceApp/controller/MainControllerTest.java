package com.example.ScienceApp.controller;

import com.example.ScienceApp.domain.Animal;
import com.example.ScienceApp.domain.User;
import com.example.ScienceApp.repos.AnimalRepo;
import com.example.ScienceApp.repos.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MainController.class)
class MainControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserRepo userRepo;
    @MockBean
    AnimalRepo animalRepo;

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void setDefault(){
        authUser.setId(2L);
        RegistrationController.authUser = authUser;
        Mockito.when(userRepo.findByEmail(any())).thenReturn(List.of(authUser));

        user.setId(2L);
        Mockito.when(userRepo.findById(2L)).thenReturn(Optional.of(user));
    }

    User user = new User("John", "Doe", "12sadjg12", "jd@gmail.com");
    @Test
    void testUserPage_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/accounts/2")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void testUserPage_idError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/accounts/-1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.get("/accounts/null")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());
    }

    @Test
    void userPage_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/accounts/2")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        Mockito.when(userRepo.findByEmail(any())).thenReturn(List.of());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.get("/accounts/2")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.get("/accounts/2")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest2)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testUserPage_emptyError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/accounts/2")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(userRepo.findById(2L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void searchAccount_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/accounts/search")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("email", user.getEmail())
                .param("firstName", user.getFirstName())
                .param("lastName", user.getLastName())
                .param("size", "2")
                .param("from", "0");

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void searchAccount_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/accounts/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", user.getEmail())
                .param("firstName", user.getFirstName())
                .param("lastName", user.getLastName())
                .param("size", "2")
                .param("from", "0");

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        Mockito.when(userRepo.findByEmail(any())).thenReturn(List.of());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.get("/accounts/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", user.getEmail())
                .param("firstName", user.getFirstName())
                .param("lastName", user.getLastName())
                .param("size", "2")
                .param("from", "0");

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.get("/accounts/search")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest2)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void searchAccount_sizeError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/accounts/search")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("email", user.getEmail())
                .param("firstName", user.getFirstName())
                .param("lastName", user.getLastName())
                .param("size", "0")
                .param("from", "0");

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchAccount_fromError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/accounts/search")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("email", user.getEmail())
                .param("firstName", user.getFirstName())
                .param("lastName", user.getLastName())
                .param("size", "2")
                .param("from", "-1");

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    User authUser = new User("John", "Doe", "12sadjg12", "jd@gmail.com");

    @Test
    void editUser_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/accounts/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        Mockito.when(userRepo.findByEmail(any())).thenReturn(List.of());

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void editUser_bindingError() throws Exception {
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
    void editUser_idError() throws Exception {
        Mockito.when(userRepo.findByEmail(any())).thenReturn(List.of());

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/accounts/null")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.put("/accounts/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());
    }

    @Test
    void editUser_wrongPassword() throws Exception {
        Mockito.when(userRepo.findByEmail(any())).thenReturn(List.of());

        User user1 = new User("John", "Doe", "@# v2", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/accounts/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user1));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void editUser_nonAuth() throws Exception {
        Mockito.when(userRepo.findByEmail(any())).thenReturn(List.of());

        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/accounts/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }
    @Test
    void editUser_wrongId() throws Exception {
        Mockito.when(userRepo.findByEmail(any())).thenReturn(List.of());

        RegistrationController.authUser.setId(1L);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/accounts/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));
    }

    @Test
    void editUser_wrongEmail() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/accounts/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        Mockito.when(userRepo.findByEmail(any())).thenReturn(List.of(user));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }


    @Test
    void deleteUser_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/accounts/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/accounts/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        User user1 = new User("John", "Doe", "@# v2", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.delete("/accounts/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user1));

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteUser_idError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/accounts/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.delete("/accounts/null")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());

        RegistrationController.authUser.setId(1L);
        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.delete("/accounts/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        mvc.perform(mockRequest2)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteUser_animalError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/accounts/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        Mockito.when(animalRepo.findByChipperId(any())).thenReturn(List.of(new Animal()));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_notFound() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/accounts/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        Mockito.when(userRepo.findById(2L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }
}