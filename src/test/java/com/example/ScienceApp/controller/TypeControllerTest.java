package com.example.ScienceApp.controller;

import com.example.ScienceApp.domain.Animal;
import com.example.ScienceApp.domain.Type;
import com.example.ScienceApp.domain.User;
import com.example.ScienceApp.repos.AnimalRepo;
import com.example.ScienceApp.repos.TypeRepo;
import com.example.ScienceApp.repos.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TypeController.class)
class TypeControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    TypeRepo typeRepo;

    @MockBean
    UserRepo userRepo;

    @MockBean
    AnimalRepo animalRepo;

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    Type type = new Type("Type1");
    User authUser = new User("John", "Doe", "12sadjg12", "jd@gmail.com");

    @BeforeEach
    public void setDefault(){
        authUser.setId(1L);
        RegistrationController.authUser = authUser;
        Mockito.when(userRepo.findByEmail(any())).thenReturn(List.of(authUser));

        type.setId(1L);
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(type));

        Mockito.when(typeRepo.findByType(any())).thenReturn(null);
    }

    @Test
    void addType_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals/types")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void addType_typeError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals/types")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addType_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals/types")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.post("/animals/types")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addType_error() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals/types")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        Mockito.when(typeRepo.findByType(any())).thenReturn(type);

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testTypePage_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/animals/types/1");

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void testTypePage_idError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/animals/types/-1");

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.get("/animals/types/null");

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void typePage_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/animals/types/1");

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.get("/animals/types/1");

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testTypePage_notFound() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/animals/types/1");

        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void editType_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void editType_bindingError() throws Exception {
        type.setType(" ");

        Set<ConstraintViolation<Type>> constraintViolations =
                validator.validate(type);

        assertEquals(1, constraintViolations.size());
    }

    @Test
    void editType_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.put("/animals/types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void editType_conflict() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        Mockito.when(typeRepo.findByType(any())).thenReturn(type);

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void editType_notFound() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteType_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/types/1");

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void deleteType_authFailed() throws Exception {
        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/types/1");

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.delete("/animals/types/1");

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteType_idError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/types/-1");

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.delete("/animals/types/null");

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteType_null() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/types/1");

        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.empty());

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteType_animalError() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/types/1");

        Animal animal = new Animal();
        animal.setTypes(List.of(1L));
        Mockito.when(animalRepo.findAll()).thenReturn(List.of(animal));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    Animal animal = new Animal();
    @Test
    void changeAnimalType_success() throws Exception {
        Type oldType = new Type("Type1");
        oldType.setId(1L);
        Type newType = new Type("Type2");
        newType.setId(2L);
        animal.setId(1L);
        animal.setTypes(List.of(oldType.getId()));
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/1/types")
                .param("oldTypeId", oldType.getId().toString())
                .param("newTypeId", newType.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.ofNullable(animal));
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(oldType));
        Mockito.when(typeRepo.findById(2L)).thenReturn(Optional.of(newType));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void changeAnimalType_idError() throws Exception {
        Type oldType = new Type("Type1");
        oldType.setId(1L);
        Type newType = new Type("Type2");
        newType.setId(2L);
        animal.setId(1L);
        animal.setTypes(List.of(oldType.getId()));
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/-1/types")
                .param("oldTypeId", oldType.getId().toString())
                .param("newTypeId", newType.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.ofNullable(animal));
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(oldType));
        Mockito.when(typeRepo.findById(2L)).thenReturn(Optional.of(newType));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.put("/animals/null/types")
                .param("oldTypeId", oldType.getId().toString())
                .param("newTypeId", newType.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeAnimalType_typeError() throws Exception {
        Type oldType = new Type("Type1");
        oldType.setId(1L);
        Type newType = new Type("Type2");
        newType.setId(2L);
        animal.setId(1L);
        animal.setTypes(List.of(oldType.getId()));
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/1/types")
                .param("oldTypeId", "-1")
                .param("newTypeId", newType.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.ofNullable(animal));
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(oldType));
        Mockito.when(typeRepo.findById(2L)).thenReturn(Optional.of(newType));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.put("/animals/1/types")
                .param("oldTypeId", oldType.getId().toString())
                .param("newTypeId", "-2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.put("/animals/1/types")
                .param("oldTypeId", oldType.getId().toString())
                .param("newTypeId", "null")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        mvc.perform(mockRequest2)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest3 = MockMvcRequestBuilders.put("/animals/1/types")
                .param("oldTypeId", "null")
                .param("newTypeId", newType.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        mvc.perform(mockRequest3)
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeAnimalType_authFailed() throws Exception {
        Type oldType = new Type("Type1");
        oldType.setId(1L);
        Type newType = new Type("Type2");
        newType.setId(2L);
        animal.setId(1L);
        animal.setTypes(List.of(oldType.getId()));

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.ofNullable(animal));
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(oldType));
        Mockito.when(typeRepo.findById(2L)).thenReturn(Optional.of(newType));

        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/1/types")
                .param("oldTypeId", oldType.getId().toString())
                .param("newTypeId", newType.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.put("/animals/1/types")
                .param("oldTypeId", oldType.getId().toString())
                .param("newTypeId", newType.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void changeAnimalType_notFound() throws Exception {
        Type oldType = new Type("Type1");
        oldType.setId(1L);
        Type newType = new Type("Type2");
        newType.setId(2L);
        animal.setId(1L);
        animal.setTypes(List.of(oldType.getId()));

        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(oldType));
        Mockito.when(typeRepo.findById(2L)).thenReturn(Optional.of(newType));
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/1/types")
                .param("oldTypeId", oldType.getId().toString())
                .param("newTypeId", newType.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.ofNullable(animal));
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.empty());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.put("/animals/1/types")
                .param("oldTypeId", oldType.getId().toString())
                .param("newTypeId", newType.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());

        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(oldType));
        Mockito.when(typeRepo.findById(2L)).thenReturn(Optional.empty());

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.put("/animals/1/types")
                .param("oldTypeId", oldType.getId().toString())
                .param("newTypeId", newType.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        mvc.perform(mockRequest2)
                .andExpect(status().is4xxClientError());

        Mockito.when(typeRepo.findById(2L)).thenReturn(Optional.of(newType));
        animal.setTypes(List.of());

        MockHttpServletRequestBuilder mockRequest3 = MockMvcRequestBuilders.put("/animals/1/types")
                .param("oldTypeId", oldType.getId().toString())
                .param("newTypeId", newType.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        mvc.perform(mockRequest3)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void changeAnimalType_conflict() throws Exception {
        Type oldType = new Type("Type1");
        oldType.setId(1L);
        Type newType = new Type("Type2");
        newType.setId(2L);
        animal.setId(1L);
        animal.setTypes(List.of(oldType.getId(), newType.getId()));
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/animals/1/types")
                .param("oldTypeId", oldType.getId().toString())
                .param("newTypeId", newType.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(type));

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.ofNullable(animal));
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(oldType));
        Mockito.when(typeRepo.findById(2L)).thenReturn(Optional.of(newType));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addAnimalType_success() throws Exception {
        Type oldType = new Type("Type1");
        oldType.setId(1L);
        animal.setId(1L);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals/1/types/1")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.ofNullable(animal));
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(oldType));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void addAnimalType_idError() throws Exception {
        Type oldType = new Type("Type1");
        oldType.setId(1L);
        animal.setId(1L);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals/1/types/-1")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.ofNullable(animal));
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(oldType));

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.post("/animals/1/types/null")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.post("/animals/-1/types/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest2)
                .andExpect(status().isBadRequest());
        MockHttpServletRequestBuilder mockRequest3 = MockMvcRequestBuilders.post("/animals/null/types/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest3)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addAnimalType_authFailed() throws Exception {
        Type oldType = new Type("Type1");
        oldType.setId(1L);
        animal.setId(1L);

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.ofNullable(animal));
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(oldType));

        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals/1/types/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.post("/animals/1/types/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addAnimalType_notFound() throws Exception {
        Type oldType = new Type("Type1");
        oldType.setId(1L);
        animal.setId(1L);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals/1/types/1")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.ofNullable(animal));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.post("/animals/1/types/1")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(oldType));

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addAnimalType_conflict() throws Exception {
        Type oldType = new Type("Type1");
        oldType.setId(1L);
        animal.setId(1L);
        animal.setTypes(List.of(oldType.getId()));
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/animals/1/types/1")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.ofNullable(animal));
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(oldType));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteAnimalType_success() throws Exception {
        Type type = new Type("Type1");
        type.setId(1L);
        animal.setId(1L);
        animal.setTypes(List.of(type.getId()));
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/1/types/1")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.ofNullable(animal));
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(type));

        mvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    void deleteAnimalType_idError() throws Exception {
        Type type = new Type("Type1");
        type.setId(1L);
        animal.setId(1L);
        animal.setTypes(List.of(type.getId()));

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.ofNullable(animal));
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(type));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/1/types/-1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.delete("/animals/1/types/null")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.delete("/animals/-1/types/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest2)
                .andExpect(status().isBadRequest());

        MockHttpServletRequestBuilder mockRequest3 = MockMvcRequestBuilders.delete("/animals/null/types/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest3)
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAnimalType_authFailed() throws Exception {
        Type type = new Type("Type1");
        type.setId(1L);
        animal.setId(1L);
        animal.setTypes(List.of(type.getId()));

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.ofNullable(animal));
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(type));

        RegistrationController.authUser = null;

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/1/types/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        RegistrationController.authUser = new User("John", "Doe", "12sadjg121", "jd@gmail.com");

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.delete("/animals/1/types/1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteAnimalType_notFound() throws Exception {
        Type oldType = new Type("Type1");
        oldType.setId(1L);
        animal.setId(1L);
        animal.setTypes(List.of(type.getId()));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/animals/1/types/1")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.ofNullable(animal));

        mvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.delete("/animals/1/types/1")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(typeRepo.findById(1L)).thenReturn(Optional.of(oldType));

        mvc.perform(mockRequest1)
                .andExpect(status().is4xxClientError());

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.delete("/animals/1/types/1")
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(animalRepo.findById(1L)).thenReturn(Optional.of(animal));
        animal.setTypes(List.of());

        mvc.perform(mockRequest2)
                .andExpect(status().is4xxClientError());
    }
}