package com.example.busticketingbackend.service.impl;

import com.example.busticketingbackend.dto.request.TicketPurchaseRequestDTO;
import com.example.busticketingbackend.dto.response.TicketResponseDTO;
import com.example.busticketingbackend.exception.BusinessException;
import com.example.busticketingbackend.exception.ResourceNotFoundException;
import com.example.busticketingbackend.model.BusLine;
import com.example.busticketingbackend.model.Ticket;
import com.example.busticketingbackend.model.TicketCategory;
import com.example.busticketingbackend.repository.BusLineRepository;
import com.example.busticketingbackend.repository.TicketCategoryRepository;
import com.example.busticketingbackend.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock private TicketRepository ticketRepo;
    @Mock private TicketCategoryRepository categoryRepo;
    @Mock private BusLineRepository busLineRepo;

    @InjectMocks private TicketServiceImpl service;

    private TicketCategory monthlyCategory;
    private TicketCategory singleRideCategory;
    private BusLine busLine;
    private Ticket activeTicket;

    @BeforeEach
    void setUp() {
        monthlyCategory = new TicketCategory();
        monthlyCategory.setId(1L);
        monthlyCategory.setName("Monthly Pass");
        monthlyCategory.setDurationDays(30);
        monthlyCategory.setPrice(new BigDecimal("45.00"));

        singleRideCategory = new TicketCategory();
        singleRideCategory.setId(2L);
        singleRideCategory.setName("Single Ride");
        singleRideCategory.setDurationDays(0);
        singleRideCategory.setPrice(new BigDecimal("1.50"));

        busLine = new BusLine();
        busLine.setId(1L);
        busLine.setName("Line 1 - Downtown Loop");
        busLine.setActive(true);
        busLine.setRouteStops(new ArrayList<>());

        activeTicket = new Ticket();
        activeTicket.setId(1L);
        activeTicket.setPassengerName("John Doe");
        activeTicket.setPassengerEmail("john@example.com");
        activeTicket.setTicketCategory(monthlyCategory);
        activeTicket.setBusLine(busLine);
        activeTicket.setPurchaseDate(LocalDateTime.now().minusDays(5));
        activeTicket.setValidFrom(LocalDateTime.now().minusDays(5));
        activeTicket.setExpirationDate(LocalDateTime.now().plusDays(25));
        activeTicket.setActive(true);
    }

    // ===== findAll =====

    @Test
    void findAll_withFilters_delegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ticket> page = new PageImpl<>(List.of(activeTicket), pageable, 1);

        when(ticketRepo.findFiltered("john@example.com", true, 1L, pageable)).thenReturn(page);

        Page<TicketResponseDTO> result = service.findAll("john@example.com", true, 1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPassengerName()).isEqualTo("John Doe");
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findAll_noFilters_returnsAll() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Ticket> page = new PageImpl<>(List.of(activeTicket), pageable, 1);

        when(ticketRepo.findFiltered(null, null, null, pageable)).thenReturn(page);

        Page<TicketResponseDTO> result = service.findAll(null, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    // ===== findById =====

    @Test
    void findById_existing_returnsDto() {
        when(ticketRepo.findById(1L)).thenReturn(Optional.of(activeTicket));

        TicketResponseDTO dto = service.findById(1L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getPassengerName()).isEqualTo("John Doe");
        assertThat(dto.getCategoryName()).isEqualTo("Monthly Pass");
        assertThat(dto.getBusLineName()).isEqualTo("Line 1 - Downtown Loop");
        assertThat(dto.getActive()).isTrue();
    }

    @Test
    void findById_nonExisting_throwsException() {
        when(ticketRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findById_nullBusLine_returnsDtoWithNullLineName() {
        activeTicket.setBusLine(null);
        when(ticketRepo.findById(1L)).thenReturn(Optional.of(activeTicket));

        TicketResponseDTO dto = service.findById(1L);

        assertThat(dto.getBusLineName()).isNull();
    }

    // ===== purchase =====

    @Test
    void purchase_monthlyPass_setsCorrectExpiration() {
        TicketPurchaseRequestDTO req = new TicketPurchaseRequestDTO();
        req.setPassengerName("Jane Doe");
        req.setPassengerEmail("jane@example.com");
        req.setTicketCategoryId(1L);
        req.setBusLineId(1L);

        when(categoryRepo.findById(1L)).thenReturn(Optional.of(monthlyCategory));
        when(busLineRepo.findById(1L)).thenReturn(Optional.of(busLine));
        when(ticketRepo.save(any(Ticket.class))).thenAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        TicketResponseDTO result = service.purchase(req);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getPassengerName()).isEqualTo("Jane Doe");
        assertThat(result.getCategoryName()).isEqualTo("Monthly Pass");

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepo).save(captor.capture());
        Ticket saved = captor.getValue();
        assertThat(saved.getExpirationDate()).isAfter(saved.getValidFrom().plusDays(29));
        assertThat(saved.getActive()).isTrue();
    }

    @Test
    void purchase_singleRide_sets2HourExpiration() {
        TicketPurchaseRequestDTO req = new TicketPurchaseRequestDTO();
        req.setPassengerName("Bob");
        req.setPassengerEmail("bob@example.com");
        req.setTicketCategoryId(2L);

        when(categoryRepo.findById(2L)).thenReturn(Optional.of(singleRideCategory));
        when(ticketRepo.save(any(Ticket.class))).thenAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            t.setId(11L);
            return t;
        });

        service.purchase(req);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepo).save(captor.capture());
        Ticket saved = captor.getValue();
        // Single ride: expiration = validFrom + 2 hours
        assertThat(saved.getExpirationDate())
                .isCloseTo(saved.getValidFrom().plusHours(2), within(1, java.time.temporal.ChronoUnit.SECONDS));
    }

    @Test
    void purchase_withCustomValidFrom_usesProvidedDate() {
        LocalDateTime customStart = LocalDateTime.of(2026, 3, 1, 8, 0);

        TicketPurchaseRequestDTO req = new TicketPurchaseRequestDTO();
        req.setPassengerName("Alice");
        req.setPassengerEmail("alice@example.com");
        req.setTicketCategoryId(1L);
        req.setValidFrom(customStart);

        when(categoryRepo.findById(1L)).thenReturn(Optional.of(monthlyCategory));
        when(ticketRepo.save(any(Ticket.class))).thenAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            t.setId(12L);
            return t;
        });

        service.purchase(req);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepo).save(captor.capture());
        Ticket saved = captor.getValue();
        assertThat(saved.getValidFrom()).isEqualTo(customStart);
        assertThat(saved.getExpirationDate()).isEqualTo(customStart.plusDays(30));
    }

    @Test
    void purchase_nullBusLineId_savesWithNullBusLine() {
        TicketPurchaseRequestDTO req = new TicketPurchaseRequestDTO();
        req.setPassengerName("Charlie");
        req.setPassengerEmail("charlie@example.com");
        req.setTicketCategoryId(1L);
        req.setBusLineId(null);

        when(categoryRepo.findById(1L)).thenReturn(Optional.of(monthlyCategory));
        when(ticketRepo.save(any(Ticket.class))).thenAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            t.setId(13L);
            return t;
        });

        service.purchase(req);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepo).save(captor.capture());
        assertThat(captor.getValue().getBusLine()).isNull();
        verify(busLineRepo, never()).findById(any());
    }

    @Test
    void purchase_invalidCategoryId_throwsException() {
        TicketPurchaseRequestDTO req = new TicketPurchaseRequestDTO();
        req.setPassengerName("X");
        req.setPassengerEmail("x@x.com");
        req.setTicketCategoryId(999L);

        when(categoryRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.purchase(req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void purchase_invalidBusLineId_throwsException() {
        TicketPurchaseRequestDTO req = new TicketPurchaseRequestDTO();
        req.setPassengerName("X");
        req.setPassengerEmail("x@x.com");
        req.setTicketCategoryId(1L);
        req.setBusLineId(999L);

        when(categoryRepo.findById(1L)).thenReturn(Optional.of(monthlyCategory));
        when(busLineRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.purchase(req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ===== renew =====

    @Test
    void renew_activeNotExpiredTicket_extendsFromCurrentExpiration() {
        LocalDateTime originalExpiration = activeTicket.getExpirationDate();
        when(ticketRepo.findById(1L)).thenReturn(Optional.of(activeTicket));
        when(ticketRepo.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        TicketResponseDTO result = service.renew(1L);

        assertThat(result.getActive()).isTrue();
        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepo).save(captor.capture());
        Ticket saved = captor.getValue();
        // Should extend from the current expiration date since ticket hasn't expired
        assertThat(saved.getValidFrom()).isEqualTo(originalExpiration);
        assertThat(saved.getExpirationDate()).isEqualTo(originalExpiration.plusDays(30));
    }

    @Test
    void renew_expiredTicket_startsFromNow() {
        activeTicket.setExpirationDate(LocalDateTime.now().minusDays(2));
        when(ticketRepo.findById(1L)).thenReturn(Optional.of(activeTicket));
        when(ticketRepo.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        service.renew(1L);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepo).save(captor.capture());
        Ticket saved = captor.getValue();
        // Should start from now since ticket has expired
        assertThat(saved.getValidFrom()).isCloseTo(LocalDateTime.now(),
                within(2, java.time.temporal.ChronoUnit.SECONDS));
    }

    @Test
    void renew_singleRideTicket_throwsBusinessException() {
        activeTicket.setTicketCategory(singleRideCategory);
        when(ticketRepo.findById(1L)).thenReturn(Optional.of(activeTicket));

        assertThatThrownBy(() -> service.renew(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Single ride tickets cannot be renewed");
    }

    @Test
    void renew_nonExistingTicket_throwsException() {
        when(ticketRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.renew(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void renew_setsNewPurchaseDate() {
        when(ticketRepo.findById(1L)).thenReturn(Optional.of(activeTicket));
        when(ticketRepo.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        service.renew(1L);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepo).save(captor.capture());
        assertThat(captor.getValue().getPurchaseDate())
                .isCloseTo(LocalDateTime.now(), within(2, java.time.temporal.ChronoUnit.SECONDS));
    }

    // ===== cancel =====

    @Test
    void cancel_existingTicket_setsActiveToFalse() {
        when(ticketRepo.findById(1L)).thenReturn(Optional.of(activeTicket));
        when(ticketRepo.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        service.cancel(1L);

        assertThat(activeTicket.getActive()).isFalse();
        verify(ticketRepo).save(activeTicket);
    }

    @Test
    void cancel_nonExistingTicket_throwsException() {
        when(ticketRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancel(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ===== DTO mapping (computed fields) =====

    @Test
    void toDto_activeNonExpiredTicket_computesFieldsCorrectly() {
        when(ticketRepo.findById(1L)).thenReturn(Optional.of(activeTicket));

        TicketResponseDTO dto = service.findById(1L);

        assertThat(dto.getExpired()).isFalse();
        assertThat(dto.getDaysRemaining()).isGreaterThan(0);
    }

    @Test
    void toDto_expiredTicket_showsExpiredAndZeroDays() {
        activeTicket.setExpirationDate(LocalDateTime.now().minusDays(1));
        when(ticketRepo.findById(1L)).thenReturn(Optional.of(activeTicket));

        TicketResponseDTO dto = service.findById(1L);

        assertThat(dto.getExpired()).isTrue();
        assertThat(dto.getDaysRemaining()).isEqualTo(0);
    }
}