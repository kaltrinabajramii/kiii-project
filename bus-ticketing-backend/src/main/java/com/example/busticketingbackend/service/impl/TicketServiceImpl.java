package com.example.busticketingbackend.service.impl;

import com.example.busticketingbackend.dto.request.TicketPurchaseRequestDTO;
import com.example.busticketingbackend.dto.response.TicketResponseDTO;
import com.example.busticketingbackend.model.BusLine;
import com.example.busticketingbackend.model.Ticket;
import com.example.busticketingbackend.model.TicketCategory;
import com.example.busticketingbackend.repository.BusLineRepository;
import com.example.busticketingbackend.repository.TicketRepository;
import com.example.busticketingbackend.repository.TicketCategoryRepository;
import com.example.busticketingbackend.exception.ResourceNotFoundException;
import com.example.busticketingbackend.exception.BusinessException;
import com.example.busticketingbackend.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepo;
    private final TicketCategoryRepository categoryRepo;
    private final BusLineRepository busLineRepo;

    public TicketServiceImpl(TicketRepository ticketRepo, TicketCategoryRepository categoryRepo,
                             BusLineRepository busLineRepo) {
        this.ticketRepo = ticketRepo;
        this.categoryRepo = categoryRepo;
        this.busLineRepo = busLineRepo;
    }

    @Override
    public Page<TicketResponseDTO> findAll(String email, Boolean active, Long busLineId, Pageable pageable) {
        return ticketRepo.findFiltered(email, active, busLineId, pageable).map(this::toDto);
    }

    @Override
    public TicketResponseDTO findById(Long id) {
        return toDto(ticketRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id)));
    }

    @Override
    public TicketResponseDTO purchase(TicketPurchaseRequestDTO dto) {
        TicketCategory category = categoryRepo.findById(dto.getTicketCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket category not found with id: " + dto.getTicketCategoryId()));

        BusLine busLine = null;
        if (dto.getBusLineId() != null) {
            busLine = busLineRepo.findById(dto.getBusLineId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bus line not found with id: " + dto.getBusLineId()));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validFrom = dto.getValidFrom() != null ? dto.getValidFrom() : now;
        LocalDateTime expiration;
        if (category.getDurationDays() == 0) {
            expiration = validFrom.plusHours(2);
        } else {
            expiration = validFrom.plusDays(category.getDurationDays());
        }

        Ticket ticket = new Ticket();
        ticket.setPassengerName(dto.getPassengerName());
        ticket.setPassengerEmail(dto.getPassengerEmail());
        ticket.setTicketCategory(category);
        ticket.setBusLine(busLine);
        ticket.setPurchaseDate(now);
        ticket.setValidFrom(validFrom);
        ticket.setExpirationDate(expiration);
        ticket.setActive(true);

        return toDto(ticketRepo.save(ticket));
    }

    @Override
    public TicketResponseDTO renew(Long id) {
        Ticket ticket = ticketRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        if (ticket.getTicketCategory().getDurationDays() == 0) {
            throw new BusinessException("Single ride tickets cannot be renewed");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newStart = ticket.getExpirationDate().isAfter(now) ? ticket.getExpirationDate() : now;
        ticket.setValidFrom(newStart);
        ticket.setExpirationDate(newStart.plusDays(ticket.getTicketCategory().getDurationDays()));
        ticket.setPurchaseDate(now);
        ticket.setActive(true);

        return toDto(ticketRepo.save(ticket));
    }

    @Override
    public void cancel(Long id) {
        Ticket ticket = ticketRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        ticket.setActive(false);
        ticketRepo.save(ticket);
    }

    private TicketResponseDTO toDto(Ticket t) {
        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(t.getId());
        dto.setPassengerName(t.getPassengerName());
        dto.setPassengerEmail(t.getPassengerEmail());
        dto.setCategoryName(t.getTicketCategory().getName());
        dto.setBusLineName(t.getBusLine() != null ? t.getBusLine().getName() : null);
        dto.setPurchaseDate(t.getPurchaseDate());
        dto.setValidFrom(t.getValidFrom());
        dto.setExpirationDate(t.getExpirationDate());
        dto.setActive(t.getActive());

        boolean expired = LocalDateTime.now().isAfter(t.getExpirationDate());
        dto.setExpired(expired);

        long daysLeft = ChronoUnit.DAYS.between(LocalDateTime.now(), t.getExpirationDate());
        dto.setDaysRemaining(Math.max(0, daysLeft));

        return dto;
    }
}