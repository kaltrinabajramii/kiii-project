package com.example.busticketingbackend.controller;

import com.example.busticketingbackend.dto.request.TicketPurchaseRequestDTO;
import com.example.busticketingbackend.dto.response.TicketResponseDTO;
import com.example.busticketingbackend.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @GetMapping
    public Page<TicketResponseDTO> getAll(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Long busLineId,
            Pageable pageable) {
        return service.findAll(email, active, busLineId, pageable);
    }

    @GetMapping("/{id}")
    public TicketResponseDTO getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<TicketResponseDTO> purchase(@Valid @RequestBody TicketPurchaseRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.purchase(dto));
    }

    @PostMapping("/{id}/renew")
    public TicketResponseDTO renew(@PathVariable Long id) {
        return service.renew(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id) {
        service.cancel(id);
    }
}