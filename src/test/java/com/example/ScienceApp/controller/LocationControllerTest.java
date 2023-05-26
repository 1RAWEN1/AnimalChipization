package com.example.ScienceApp.controller;

import com.example.ScienceApp.domain.Animal;
import com.example.ScienceApp.domain.Location;
import com.example.ScienceApp.domain.User;
import com.example.ScienceApp.domain.VisitedLocation;
import com.example.ScienceApp.repos.AnimalRepo;
import com.example.ScienceApp.repos.LocationRepo;
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

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LocationController.class)
class LocationControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    AnimalRepo animalRepo;

    User authUser = new User("John", "Doe", "12sadjg12", "jd@gmail.com");

    @MockBean
    UserRepo userRepo;

    @MockBean
    LocationRepo locRepo;

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    Location loc = new Location(-3.2, 45.8);
    Location chippingLoc = new Location(-2.2, 45.9);
    @BeforeEach
    public void setDefault(){
        authUser.setId(1L);
        RegistrationController.authUser = authUser;
        Mockito.when(userRepo.findByEmail(any())).thenReturn(List.of(authUser));

        loc.setId(1L);
        Mockito.when(locRepo.findByLatitude(anyDouble())).thenReturn(List.of());

        loc1.setId(2L);

        chippingLoc.setId(3L);
        animal.setId(1L);
        animal.setLifeStatus("ALIVE");
        animal.setChippingLocationId(chippingLoc.getId());
        animal.setVisitedLocations(List.of(new VisitedLocation(1L, loc.getId(), new Date())));
    }

    @Test
    void addLoc_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loc));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void addLoc_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loc));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.post("/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loc));

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addLoc_conflict() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loc));

        Mockito.when(locRepo.findByLatitude(anyDouble())).thenReturn(List.of(loc));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addLoc_bindingError() throws Exception {
        loc.setLatitude(-91);

        Set<ConstraintViolation<Location>> constraintViolations =
                validator.validate(loc);

        assertEquals(1, constraintViolations.size());

        loc.setLatitude(-3.2);
        loc.setLongitude(181);

        Set<ConstraintViolation<Location>> constraintViolations1 =
                validator.validate(loc);

        assertEquals(1, constraintViolations1.size());
    }

    @Test
    void locPage_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void locPage_authFailed() throws Exception {
        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));

        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.get("/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void locPage_idError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/locations/-1")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.get("/locations/null")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());
    }

    @Test
    void locPage_notFound() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void editLoc_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/locations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loc));

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void editLoc_idError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/locations/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loc));

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.put("/locations/null")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loc));

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());

    }

    @Test
    void editLoc_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/locations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loc));

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.put("/locations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loc));

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void editLoc_conflict() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/locations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loc));

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(locRepo.findByLatitude(anyDouble())).thenReturn(List.of(loc));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void editLoc_notFound() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/locations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loc));

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    Animal animal = new Animal();

    @Test
    void deleteLoc_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(animalRepo.findAll()).thenReturn(List.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void deleteLoc_idError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/locations/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loc));

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.put("/locations/null")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loc));

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());

    }

    @Test
    void deleteLoc_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/locations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loc));

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.put("/locations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(loc));

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteLoc_notFound() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(animalRepo.findAll()).thenReturn(List.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteLoc_notVisited() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        animal.setVisitedLocations(List.of());

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(animalRepo.findAll()).thenReturn(List.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void animalLoc_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/animals/1/locations?size=2&from=0&startDateTime=2023-03-7&endDateTime=2023-03-20")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void animalLoc_badRequest() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/animals/1/locations?size=2&from=0&startDateTime=2023-03-7&endDateTime=null")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.get("/animals/1/locations?size=2&from=0&startDateTime=2023-03-7&endDateTime=time")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.get("/animals/1/locations?size=2&from=0&startDateTime=null&endDateTime=2023-03-20")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest2)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest3 = MockMvcRequestBuilders.get("/animals/1/locations?size=2&from=0&startDateTime=time&endDateTime=2023-03-20")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest3)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest4 = MockMvcRequestBuilders.get("/animals/1/locations?size=null&from=0&startDateTime=2023-03-7&endDateTime=2023-03-20")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest4)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest5 = MockMvcRequestBuilders.get("/animals/1/locations?size=0&from=0&startDateTime=2023-03-7&endDateTime=2023-03-20")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest5)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest6 = MockMvcRequestBuilders.get("/animals/1/locations?size=2&from=null&startDateTime=2023-03-7&endDateTime=2023-03-20")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest6)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest7 = MockMvcRequestBuilders.get("/animals/1/locations?size=2&from=-1&startDateTime=2023-03-7&endDateTime=2023-03-20")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest7)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest8 = MockMvcRequestBuilders.get("/animals/null/locations?size=2&from=0&startDateTime=2023-03-7&endDateTime=2023-03-20")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest8)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest9 = MockMvcRequestBuilders.get("/animals/-1/locations?size=2&from=0&startDateTime=2023-03-7&endDateTime=2023-03-20")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest9)
                .andExpect(status().isBadRequest());
    }

    @Test
    void animalLoc_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/animals/1/locations")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.get("/animals/1/locations")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void animalLoc_notFound() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/animals/1/locations?size=2&from=0&startDateTime=2023-03-7&endDateTime=2023-03-20")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addAnimalLoc_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals/1/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        animal.setVisitedLocations(List.of(new VisitedLocation(2L, chippingLoc.getId(), new Date())));
        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void addAnimalLoc_deadAnimal() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals/1/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        animal.setLifeStatus("DEAD");
        animal.setVisitedLocations(List.of(new VisitedLocation(2L, chippingLoc.getId(), new Date())));
        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addAnimalLoc_idError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals/null/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        animal.setVisitedLocations(List.of());
        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.post("/animals/-1/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.post("/animals/1/locations/null")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest2)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest3 = MockMvcRequestBuilders.post("/animals/1/locations/-1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest3)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addAnimalLoc_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals/1/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        animal.setVisitedLocations(List.of());
        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.post("/animals/1/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addAnimalLoc_notFound() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals/1/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        animal.setVisitedLocations(List.of(new VisitedLocation(2L, chippingLoc.getId(), new Date())));
        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.post("/animals/1/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addAnimalLoc_wrongLoc() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals/1/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        animal.setChippingLocationId(loc.getId());
        animal.setVisitedLocations(List.of(new VisitedLocation(2L, chippingLoc.getId(), new Date())));
        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.post("/animals/1/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        animal.setChippingLocationId(chippingLoc.getId());
        animal.setVisitedLocations(List.of(new VisitedLocation(1L, loc.getId(), new Date())));

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAnimalLoc_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/1/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        animal.setVisitedLocations(List.of(new VisitedLocation(1L, loc.getId(), new Date())));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void deleteAnimalLoc_idError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/null/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        animal.setVisitedLocations(List.of(new VisitedLocation(1L, loc.getId(), new Date())));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.delete("/animals/-1/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.delete("/animals/1/locations/null")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest2)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest3 = MockMvcRequestBuilders.delete("/animals/1/locations/-1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest3)
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAnimalLoc_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/1/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        animal.setVisitedLocations(List.of(new VisitedLocation(1L, loc.getId(), new Date())));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.delete("/animals/1/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteAnimalLoc_notFound() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/1/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        animal.setVisitedLocations(List.of(new VisitedLocation(1L, loc.getId(), new Date())));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteAnimalLoc_wrongLocId() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/1/locations/1")
                .accept(MediaType.APPLICATION_JSON);

        animal.setVisitedLocations(List.of());
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    Location loc1 = new Location(-1.2, 45.9);
    @Test
    void changeAnimalLoc_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/1/locations")
                .param("visitedLocationPointId", loc.getId().toString())
                .param("locationPointId", loc1.getId().toString())
                .accept(MediaType.APPLICATION_JSON);

        animal.setVisitedLocations(List.of(new VisitedLocation(1L, loc.getId(), new Date())));
        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(locRepo.findById(2L)).thenReturn(Optional.of(loc1));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void changeAnimalLoc_idError() throws Exception {
        animal.setVisitedLocations(List.of(new VisitedLocation(1L, loc.getId(), new Date())));
        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(locRepo.findById(2L)).thenReturn(Optional.of(loc1));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/null/locations")
                .param("visitedLocationPointId", loc.getId().toString())
                .param("locationPointId", loc1.getId().toString())
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.put("/animals/-1/locations")
                .param("visitedLocationPointId", loc.getId().toString())
                .param("locationPointId", loc1.getId().toString())
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.put("/animals/1/locations")
                .param("visitedLocationPointId", "null")
                .param("locationPointId", loc1.getId().toString())
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest2)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest3 = MockMvcRequestBuilders.put("/animals/1/locations")
                .param("visitedLocationPointId", "-1")
                .param("locationPointId", loc1.getId().toString())
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest3)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest4 = MockMvcRequestBuilders.put("/animals/1/locations")
                .param("visitedLocationPointId", loc.getId().toString())
                .param("locationPointId", "null")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest4)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest5 = MockMvcRequestBuilders.put("/animals/1/locations")
                .param("visitedLocationPointId", loc.getId().toString())
                .param("locationPointId", "-1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest5)
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeAnimalLoc_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/1/locations")
                .param("visitedLocationPointId", loc.getId().toString())
                .param("locationPointId", loc1.getId().toString())
                .accept(MediaType.APPLICATION_JSON);

        animal.setVisitedLocations(List.of(new VisitedLocation(1L, loc.getId(), new Date())));
        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(locRepo.findById(2L)).thenReturn(Optional.of(loc1));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.delete("/animals/1/locations")
                .param("visitedLocationPointId", loc.getId().toString())
                .param("locationPointId", loc1.getId().toString())
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void changeAnimalLoc_notFound() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/1/locations")
                .param("visitedLocationPointId", loc.getId().toString())
                .param("locationPointId", loc1.getId().toString())
                .accept(MediaType.APPLICATION_JSON);

        animal.setVisitedLocations(List.of(new VisitedLocation(1L, loc.getId(), new Date())));
        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(locRepo.findById(2L)).thenReturn(Optional.of(loc1));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.put("/animals/1/locations")
                .param("visitedLocationPointId", loc.getId().toString())
                .param("locationPointId", loc1.getId().toString())
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.put("/animals/1/locations")
                .param("visitedLocationPointId", loc.getId().toString())
                .param("locationPointId", loc1.getId().toString())
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(locRepo.findById(2L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest2)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void changeAnimalLoc_wrongOldId() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/1/locations")
                .param("visitedLocationPointId", loc.getId().toString())
                .param("locationPointId", loc1.getId().toString())
                .accept(MediaType.APPLICATION_JSON);

        animal.setVisitedLocations(List.of());
        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(locRepo.findById(2L)).thenReturn(Optional.of(loc1));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void changeAnimalLoc_wrongNewId() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/1/locations")
                .param("visitedLocationPointId", loc.getId().toString())
                .param("locationPointId", loc1.getId().toString())
                .accept(MediaType.APPLICATION_JSON);

        animal.setVisitedLocations(List.of(new VisitedLocation(1L, loc.getId(), new Date()), new VisitedLocation(2L, loc1.getId(), new Date())));
        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(locRepo.findById(2L)).thenReturn(Optional.of(loc1));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void changeAnimalLoc_wrongChange() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/1/locations")
                .param("visitedLocationPointId", loc.getId().toString())
                .param("locationPointId", loc1.getId().toString())
                .accept(MediaType.APPLICATION_JSON);

        animal.setVisitedLocations(List.of(new VisitedLocation(1L, loc.getId(), new Date())));
        animal.setChippingLocationId(loc1.getId());
        Mockito.when(locRepo.findById(1L)).thenReturn(Optional.of(loc));
        Mockito.when(locRepo.findById(2L)).thenReturn(Optional.of(loc1));
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }
}