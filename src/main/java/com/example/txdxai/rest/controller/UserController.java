package com.example.txdxai.rest.controller;

import com.example.txdxai.core.model.User;
import com.example.txdxai.core.service.UserService;
import com.example.txdxai.rest.dto.UserDto;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @WithSpan("user.listAll")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public List<UserDto> list(
            @RequestParam(required = false) Long companyId) {

        List<User> users = (companyId == null)
                ? userService.findAll()
                : userService.findAllByCompany(companyId);

        return users.stream()
                .map(u -> modelMapper.map(u, UserDto.class))
                .toList();
    }

    @WithSpan("user.create")
    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserDto dto) {
        User entity = modelMapper.map(dto, User.class);
        User saved  = userService.create(entity);
        UserDto responseDto = modelMapper.map(saved, UserDto.class);
        return ResponseEntity
                .created(URI.create("/api/users/" + saved.getId()))
                .body(responseDto);
    }

    @WithSpan("user.getById")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        // Ya lanza ResourceNotFoundException si no existe
        User user = userService.findById(id);
        return ResponseEntity.ok(modelMapper.map(user, UserDto.class));
    }

    @WithSpan("user.update")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(
            @PathVariable Long id,
            @RequestBody UserDto dto) {

        User entity = modelMapper.map(dto, User.class);
        entity.setId(id);
        User updated = userService.update(id, entity);
        return ResponseEntity.ok(modelMapper.map(updated, UserDto.class));
    }

    @WithSpan("user.delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}