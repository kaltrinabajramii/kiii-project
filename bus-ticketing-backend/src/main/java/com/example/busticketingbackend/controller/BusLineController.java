package com.example.busticketingbackend.controller;

import com.example.busticketingbackend.dto.request.BusLineRequestDTO;
import com.example.busticketingbackend.dto.request.RouteRequestDTO;
import com.example.busticketingbackend.dto.response.BusLineResponseDTO;
import com.example.busticketingbackend.dto.response.BusLineResponseDTO.RouteStopDTO;
import com.example.busticketingbackend.service.BusLineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bus-lines")
public class BusLineController {

    private final BusLineService service;

    public BusLineController(BusLineService service) {
        this.service = service;
    }

    @GetMapping
    public List<BusLineResponseDTO> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public BusLineResponseDTO getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<BusLineResponseDTO> create(@Valid @RequestBody BusLineRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public BusLineResponseDTO update(@PathVariable Long id, @Valid @RequestBody BusLineRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/{id}/route")
    public BusLineResponseDTO setRoute(@PathVariable Long id, @Valid @RequestBody RouteRequestDTO dto) {
        return service.setRoute(id, dto);
    }

    @GetMapping("/{id}/route")
    public List<RouteStopDTO> getRoute(@PathVariable Long id) {
        return service.getRoute(id);
    }
}
