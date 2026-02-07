package com.example.busticketingbackend.controller;

import com.example.busticketingbackend.dto.request.BusStopRequestDTO;
import com.example.busticketingbackend.dto.response.BusStopResponseDTO;
import com.example.busticketingbackend.service.BusStopService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bus-stops")
public class BusStopController {

    private final BusStopService service;

    public BusStopController(BusStopService service) {
        this.service = service;
    }

    @GetMapping
    public List<BusStopResponseDTO> getAll() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<BusStopResponseDTO> create(@Valid @RequestBody BusStopRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public BusStopResponseDTO update(@PathVariable Long id, @Valid @RequestBody BusStopRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
