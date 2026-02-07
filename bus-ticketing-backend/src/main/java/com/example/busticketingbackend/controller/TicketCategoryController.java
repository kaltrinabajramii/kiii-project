package com.example.busticketingbackend.controller;

import com.example.busticketingbackend.dto.request.TicketCategoryRequestDTO;
import com.example.busticketingbackend.dto.response.TicketCategoryResponseDTO;
import com.example.busticketingbackend.service.TicketCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-categories")
public class TicketCategoryController {

    private final TicketCategoryService service;

    public TicketCategoryController(TicketCategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<TicketCategoryResponseDTO> getAll() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<TicketCategoryResponseDTO> create(@Valid @RequestBody TicketCategoryRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public TicketCategoryResponseDTO update(@PathVariable Long id,
                                            @Valid @RequestBody TicketCategoryRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
