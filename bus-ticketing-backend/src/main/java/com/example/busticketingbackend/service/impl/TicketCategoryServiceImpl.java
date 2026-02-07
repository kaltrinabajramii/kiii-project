package com.example.busticketingbackend.service.impl;

import com.example.busticketingbackend.dto.request.TicketCategoryRequestDTO;
import com.example.busticketingbackend.dto.response.TicketCategoryResponseDTO;
import com.example.busticketingbackend.model.TicketCategory;
import com.example.busticketingbackend.repository.TicketCategoryRepository;
import com.example.busticketingbackend.exception.ResourceNotFoundException;
import com.example.busticketingbackend.service.TicketCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketCategoryServiceImpl implements TicketCategoryService {

    private final TicketCategoryRepository repo;

    public TicketCategoryServiceImpl(TicketCategoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<TicketCategoryResponseDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public TicketCategoryResponseDTO create(TicketCategoryRequestDTO dto) {
        TicketCategory cat = new TicketCategory();
        cat.setName(dto.getName());
        cat.setDurationDays(dto.getDurationDays());
        cat.setPrice(dto.getPrice());
        cat.setDescription(dto.getDescription());
        return toDto(repo.save(cat));
    }

    @Override
    public TicketCategoryResponseDTO update(Long id, TicketCategoryRequestDTO dto) {
        TicketCategory cat = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket category not found with id: " + id));
        cat.setName(dto.getName());
        cat.setDurationDays(dto.getDurationDays());
        cat.setPrice(dto.getPrice());
        cat.setDescription(dto.getDescription());
        return toDto(repo.save(cat));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Ticket category not found with id: " + id);
        }
        repo.deleteById(id);
    }

    private TicketCategoryResponseDTO toDto(TicketCategory cat) {
        TicketCategoryResponseDTO dto = new TicketCategoryResponseDTO();
        dto.setId(cat.getId());
        dto.setName(cat.getName());
        dto.setDurationDays(cat.getDurationDays());
        dto.setPrice(cat.getPrice());
        dto.setDescription(cat.getDescription());
        return dto;
    }
}
