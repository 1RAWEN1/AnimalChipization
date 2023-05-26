package com.example.ScienceApp.controller;

import com.example.ScienceApp.domain.*;
import com.example.ScienceApp.repos.AnimalRepo;
import com.example.ScienceApp.repos.LocationRepo;
import com.example.ScienceApp.repos.TypeRepo;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AnimalController.class)
class AnimalControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    AnimalRepo animalRepo;

    @MockBean
    LocationRepo locationRepo;

    User authUser = new User("John", "Doe", "12sadjg12", "jd@gmail.com");

    @MockBean
    UserRepo userRepo;

    @MockBean
    TypeRepo typeRepo;

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    Animal animal = new Animal();
    Location loc = new Location(-3.2, 45.8);
    Type type = new Type("Type1");
    Location chippingLoc = new Location(-2.2, 45.9);

    @BeforeEach
    public void setDefault() throws Exception {
        authUser.setId(1L);
        RegistrationController.authUser = authUser;
        Mockito.when(userRepo.findByEmail(any())).thenReturn(List.of(authUser));

        chippingLoc.setId(1L);
        loc.setId(2L);
        type.setId(1L);

        animal.setChipperId(authUser.getId());
        animal.setId(1L);
        animal.setGender("MALE");
        animal.setLifeStatus("ALIVE");
        animal.setLength(3.2f);
        animal.setHeight(1.2f);
        animal.setWeight(150.6f);

        animal.setChippingLocationId(chippingLoc.getId());
        animal.setTypes(List.of(type.getId()));
        animal.setVisitedLocations(List.of(new VisitedLocation(1L, loc.getId(), new Date())));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(type));
        Mockito.when(locationRepo.findById(1L)).thenReturn(Optional.of(chippingLoc));
        Mockito.when(userRepo.findById(authUser.getId())).thenReturn(Optional.of(authUser));

        Mockito.when(locationRepo.findAll()).thenReturn(List.of());
        Mockito.when(userRepo.findAll()).thenReturn(List.of());
    }

    @Test
    void animalInfo_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/animals/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void animalInfo_idError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/animals/null")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.get("/animals/-1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());
    }

    @Test
    void animalInfo_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/animals/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.get("/animals/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void animalInfo_notFound() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/animals/1")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteAnimal_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void deleteAnimal_idError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/null")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.delete("/animals/-1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAnimal_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/null")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.delete("/animals/null")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteAnimal_notFound() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/1")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void searchAnimal_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/animals/search")
                .accept(MediaType.APPLICATION_JSON)
                .param("startDateTime", "2023-03-10")
                .param("endDateTime", "2023-03-10")
                .param("size", "2")
                .param("from", "0")
                .param("chippingLocationId", "1")
                .param("chipperId", "1")
                .param("gender", "FEMALE")
                .param("lifeStatus", "ALIVE");

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void searchAnimal_badRequest() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/animals/search")
                .accept(MediaType.APPLICATION_JSON)
                .param("startDateTime", "2023-03-10")
                .param("endDateTime", "2023-03-10")
                .param("size", "null")
                .param("from", "0")
                .param("chippingLocationId", "1")
                .param("chipperId", "1")
                .param("gender", "FEMALE")
                .param("lifeStatus", "ALIVE");

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.get("/animals/search")
                .accept(MediaType.APPLICATION_JSON)
                .param("startDateTime", "2023-03-10")
                .param("endDateTime", "2023-03-10")
                .param("size", "-1")
                .param("from", "0")
                .param("chippingLocationId", "1")
                .param("chipperId", "1")
                .param("gender", "FEMALE")
                .param("lifeStatus", "ALIVE");

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.get("/animals/search")
                .accept(MediaType.APPLICATION_JSON)
                .param("startDateTime", "2023-03-10")
                .param("endDateTime", "2023-03-10")
                .param("size", "2")
                .param("from", "null")
                .param("chippingLocationId", "1")
                .param("chipperId", "1")
                .param("gender", "FEMALE")
                .param("lifeStatus", "ALIVE");

        mvc.perform(mockRequest2)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest3 = MockMvcRequestBuilders.get("/animals/search")
                .accept(MediaType.APPLICATION_JSON)
                .param("startDateTime", "2023-03-10")
                .param("endDateTime", "2023-03-10")
                .param("size", "2")
                .param("from", "-1")
                .param("chippingLocationId", "1")
                .param("chipperId", "1")
                .param("gender", "FEMALE")
                .param("lifeStatus", "ALIVE");

        mvc.perform(mockRequest3)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest4 = MockMvcRequestBuilders.get("/animals/search")
                .accept(MediaType.APPLICATION_JSON)
                .param("startDateTime", "2023-03-10")
                .param("endDateTime", "2023-03-10")
                .param("size", "2")
                .param("from", "0")
                .param("chippingLocationId", "1")
                .param("chipperId", "1")
                .param("gender", "RANDOM")
                .param("lifeStatus", "ALIVE");

        mvc.perform(mockRequest4)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest5 = MockMvcRequestBuilders.get("/animals/search")
                .accept(MediaType.APPLICATION_JSON)
                .param("startDateTime", "2023-03-10")
                .param("endDateTime", "2023-03-10")
                .param("size", "2")
                .param("from", "0")
                .param("chippingLocationId", "1")
                .param("chipperId", "1")
                .param("gender", "FEMALE")
                .param("lifeStatus", "RANDOM");

        mvc.perform(mockRequest5)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest6 = MockMvcRequestBuilders.get("/animals/search")
                .accept(MediaType.APPLICATION_JSON)
                .param("startDateTime", "2023.03.10")
                .param("endDateTime", "2023-03-10")
                .param("size", "2")
                .param("from", "0")
                .param("chippingLocationId", "1")
                .param("chipperId", "1")
                .param("gender", "FEMALE")
                .param("lifeStatus", "ALIVE");

        mvc.perform(mockRequest6)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest7 = MockMvcRequestBuilders.get("/animals/search")
                .accept(MediaType.APPLICATION_JSON)
                .param("startDateTime", "2023-03-10")
                .param("endDateTime", "2023.03.10")
                .param("size", "2")
                .param("from", "0")
                .param("chippingLocationId", "1")
                .param("chipperId", "1")
                .param("gender", "FEMALE")
                .param("lifeStatus", "ALIVE");

        mvc.perform(mockRequest7)
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchAnimal_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/animals/search")
                .accept(MediaType.APPLICATION_JSON)
                .param("startDateTime", "2023-03-10")
                .param("endDateTime", "2023-03-10")
                .param("size", "null")
                .param("from", "0")
                .param("chippingLocationId", "1")
                .param("chipperId", "1")
                .param("gender", "FEMALE")
                .param("lifeStatus", "ALIVE");

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.get("/animals/search")
                .accept(MediaType.APPLICATION_JSON)
                .param("startDateTime", "2023-03-10")
                .param("endDateTime", "2023-03-10")
                .param("size", "null")
                .param("from", "0")
                .param("chippingLocationId", "1")
                .param("chipperId", "1")
                .param("gender", "FEMALE")
                .param("lifeStatus", "ALIVE");

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addAnimal_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void addAnimal_notFound() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addAnimal_notFound1() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

       Mockito.when(locationRepo.findById(1L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addAnimal_notFound2() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        Mockito.when(userRepo.findById(authUser.getId())).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addAnimal_badRequest() throws Exception {
        animal.setTypes(List.of());

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        animal.setTypes(List.of(type.getId()));
        animal.setGender("RANDOM");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addAnimal_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.post("/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addAnimal_bindingError() {
        animal.setChippingLocationId(null);

        Set<ConstraintViolation<Animal>> constraintViolations =
                validator.validate(animal);

        assertEquals(1, constraintViolations.size());

        animal.setChippingLocationId(chippingLoc.getId());
        animal.setTypes(null);

        Set<ConstraintViolation<Animal>> constraintViolations1 =
                validator.validate(animal);

        assertEquals(1, constraintViolations1.size());

        animal.setTypes(List.of(type.getId()));
        animal.setVisitedLocations(null);

        Set<ConstraintViolation<Animal>> constraintViolations2 =
                validator.validate(animal);

        assertEquals(1, constraintViolations2.size());

        animal.setVisitedLocations(List.of(new VisitedLocation(1L, loc.getId(), new Date())));
        animal.setLength(0f);

        Set<ConstraintViolation<Animal>> constraintViolations3 =
                validator.validate(animal);

        assertEquals(1, constraintViolations3.size());

        animal.setLength(3.2f);
        animal.setHeight(0f);

        Set<ConstraintViolation<Animal>> constraintViolations4 =
                validator.validate(animal);

        assertEquals(1, constraintViolations4.size());

        animal.setHeight(1.2f);
        animal.setWeight(0f);

        Set<ConstraintViolation<Animal>> constraintViolations5 =
                validator.validate(animal);

        assertEquals(1, constraintViolations5.size());
    }

    @Test
    void editAnimal_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void editAnimal_badRequest() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/null")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.put("/animals/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());

        animal.setLifeStatus("RANDOM");

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.put("/animals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        mvc.perform(mockRequest2)
                .andExpect(status().isBadRequest());

        animal.setLifeStatus("ALIVE");
        animal.setGender("RANDOM");

        MockHttpServletRequestBuilder mockRequest3 = MockMvcRequestBuilders.put("/animals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        mvc.perform(mockRequest3)
                .andExpect(status().isBadRequest());
    }

    @Test
    void editAnimal_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.put("/animals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void editAnimal_notFound1() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void editAnimal_notFound2() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        Mockito.when(userRepo.findById(1L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void editAnimal_notFound3() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(animal));

        Mockito.when(locationRepo.findById(1L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }
}