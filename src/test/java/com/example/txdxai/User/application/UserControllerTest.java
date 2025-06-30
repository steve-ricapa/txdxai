package com.example.txdxai.User.application;

import com.example.txdxai.auth.config.JwtService;
import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.model.User;
import com.example.txdxai.core.model.Role;
import com.example.txdxai.core.service.UserService;
import com.example.txdxai.rest.controller.UserController;
import com.example.txdxai.rest.dto.UserDto;
import com.example.txdxai.rest.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@WebMvcTest(UserController.class)
class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private ModelMapper modelMapper; // Necesario si el Controller lo usa
//
//    // ... (dependencias anteriores)
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void getUser_ShouldIncludeAllDtoFields() throws Exception {
//        // Configurar User con Company y Rol
//        User user = new User();
//        user.setId(1L);
//        user.setUsername("fulluser");
//        user.setEmail("full@example.com");
//        Company company = new Company();
//        company.setId(200L);
//        user.setCompany(company);
//        user.setRole(Role.USER);
//
//        // Mockear servicios
//        Mockito.when(userService.findById(1L)).thenReturn(user);
//        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(
//                new UserDto(1L, "fulluser", "full@example.com", 200L, "MANAGER")
//        );
//
//        // Verificar respuesta JSON completa
//        mockMvc.perform(get("/api/users/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.username").value("fulluser"))
//                .andExpect(jsonPath("$.email").value("full@example.com"))
//                .andExpect(jsonPath("$.companyId").value(200))
//                .andExpect(jsonPath("$.roleName").value("MANAGER"));
//    }
//
//    @Test
//    void createUser_ShouldHandleCompanyAndRole() throws Exception {
//        UserDto requestDto = new UserDto(
//                null,
//                "newuser",
//                "new@example.com",
//                300L,
//                "USER"
//        );
//
//        User savedUser = new User();
//        savedUser.setId(2L);
//        savedUser.setCompany(new Company(300L));
//        savedUser.setRole(Role.USER);
//
//        Mockito.when(userService.create(Mockito.any(User.class))).thenReturn(savedUser);
//        Mockito.when(modelMapper.map(requestDto, User.class)).thenReturn(new User());
//        Mockito.when(modelMapper.map(savedUser, UserDto.class)).thenReturn(
//                new UserDto(2L, "newuser", "new@example.com", 300L, "USER")
//        );
//
//        mockMvc.perform(post("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                   {
//                       "username": "newuser",
//                       "email": "new@example.com",
//                       "companyId": 300,
//                       "roleName": "USER"
//                   }
//                   """))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.companyId").value(300));
//    }
}
