package com.example.busticketingbackend.service;

import com.example.busticketingbackend.dto.request.TicketCategoryRequestDTO;
import com.example.busticketingbackend.dto.response.TicketCategoryResponseDTO;

import java.util.List;

public interface TicketCategoryService {
    List<TicketCategoryResponseDTO> findAll();
    TicketCategoryResponseDTO create(TicketCategoryRequestDTO dto);
    TicketCategoryResponseDTO update(Long id, TicketCategoryRequestDTO dto);
    void delete(Long id);
}