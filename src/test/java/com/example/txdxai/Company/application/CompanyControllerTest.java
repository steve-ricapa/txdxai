package com.example.txdxai.Company.application;

import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.service.CompanyService;
import com.example.txdxai.rest.controller.CompanyController;
import com.example.txdxai.rest.dto.CompanyDto;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.modelmapper.ModelMapper;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

@WebMvcTest(CompanyController.class)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void getById_ShouldReturn404WhenNotFound() throws Exception {
        // Arrange
        Mockito.when(companyService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/companies/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturn201WithLocationHeader() throws Exception {
        // Arrange
        CompanyDto requestDto = new CompanyDto(null, "Startup");
        Company savedCompany = new Company();
        savedCompany.setId(1L);
        savedCompany.setName("Startup");

        Mockito.when(modelMapper.map(requestDto, Company.class)).thenReturn(new Company());
        Mockito.when(companyService.create(Mockito.any(Company.class))).thenReturn(savedCompany);
        Mockito.when(modelMapper.map(savedCompany, CompanyDto.class))
                .thenReturn(new CompanyDto(1L, "Startup"));

        // Act & Assert
        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Startup\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/companies/1"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_ShouldReturnUpdatedCompany() throws Exception {
        // Arrange
        CompanyDto updateDto = new CompanyDto(1L, "UpdatedName");
        Company updatedCompany = new Company();
        updatedCompany.setId(1L);
        updatedCompany.setName("UpdatedName");

        Mockito.when(modelMapper.map(updateDto, Company.class)).thenReturn(updatedCompany);
        Mockito.when(companyService.update(1L, updatedCompany)).thenReturn(updatedCompany);
        Mockito.when(modelMapper.map(updatedCompany, CompanyDto.class))
                .thenReturn(new CompanyDto(1L, "UpdatedName"));

        // Act & Assert
        mockMvc.perform(put("/api/companies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"UpdatedName\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedName"));
    }
}
