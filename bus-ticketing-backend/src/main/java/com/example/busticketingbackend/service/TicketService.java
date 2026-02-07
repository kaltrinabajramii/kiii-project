package com.example.busticketingbackend.service;

import com.example.busticketingbackend.dto.request.TicketPurchaseRequestDTO;
import com.example.busticketingbackend.dto.response.TicketResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketService {
    Page<TicketResponseDTO> findAll(String email, Boolean active, Long busLineId, Pageable pageable);
    TicketResponseDTO findById(Long id);
    TicketResponseDTO purchase(TicketPurchaseRequestDTO dto);
    TicketResponseDTO renew(Long id);
    void cancel(Long id);
}