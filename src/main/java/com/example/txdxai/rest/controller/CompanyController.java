package com.example.txdxai.rest.controller;

import com.example.txdxai.core.model.Company;
import com.example.txdxai.core.service.CompanyService;
import com.example.txdxai.rest.dto.CompanyDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<CompanyDto> listAll() {
        return companyService.findAll().stream()
                .map(c -> modelMapper.map(c, CompanyDto.class))
                .toList();
    }

    @PostMapping
    public ResponseEntity<CompanyDto> create(@RequestBody CompanyDto dto) {
        Company entity = modelMapper.map(dto, Company.class);
        Company saved = companyService.create(entity);
        CompanyDto responseDto = modelMapper.map(saved, CompanyDto.class);
        return ResponseEntity
                .created(URI.create("/api/companies/" + saved.getId()))
                .body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDto> getById(@PathVariable Long id) {
        return companyService.findById(id)
                .map(c -> modelMapper.map(c, CompanyDto.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyDto> update(
            @PathVariable Long id,
            @RequestBody CompanyDto dto) {

        Company entity = modelMapper.map(dto, Company.class);
        entity.setId(id);
        Company updated = companyService.update(id, entity);
        return ResponseEntity.ok(modelMapper.map(updated, CompanyDto.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}