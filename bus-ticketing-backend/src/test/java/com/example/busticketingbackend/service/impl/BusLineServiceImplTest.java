package com.example.busticketingbackend.service.impl;

import com.example.busticketingbackend.dto.request.BusLineRequestDTO;
import com.example.busticketingbackend.dto.request.RouteRequestDTO;
import com.example.busticketingbackend.dto.response.BusLineResponseDTO;
import com.example.busticketingbackend.exception.ResourceNotFoundException;
import com.example.busticketingbackend.model.BusLine;
import com.example.busticketingbackend.model.BusStop;
import com.example.busticketingbackend.model.RouteStop;
import com.example.busticketingbackend.repository.BusLineRepository;
import com.example.busticketingbackend.repository.BusStopRepository;
import com.example.busticketingbackend.repository.RouteStopRepository;
import com.example.busticketingbackend.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusLineServiceImplTest {

    @Mock private BusLineRepository busLineRepo;
    @Mock private BusStopRepository busStopRepo;
    @Mock private RouteStopRepository routeStopRepo;
    @Mock private TicketRepository ticketRepo;

    @InjectMocks private BusLineServiceImpl service;

    private BusLine sampleLine;
    private BusStop sampleStop;

    @BeforeEach
    void setUp() {
        sampleStop = new BusStop();
        sampleStop.setId(1L);
        sampleStop.setName("Central Station");
        sampleStop.setLatitude(41.99);
        sampleStop.setLongitude(21.43);

        sampleLine = new BusLine();
        sampleLine.setId(1L);
        sampleLine.setName("Line 1 - Downtown Loop");
        sampleLine.setDescription("Circular route");
        sampleLine.setActive(true);
        sampleLine.setRouteStops(new ArrayList<>());
    }

    // ===== findAll =====

    @Test
    void findAll_returnsAllLines() {
        BusLine line2 = new BusLine();
        line2.setId(2L);
        line2.setName("Line 2");
        line2.setActive(true);
        line2.setRouteStops(new ArrayList<>());

        when(busLineRepo.findAll()).thenReturn(List.of(sampleLine, line2));

        List<BusLineResponseDTO> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Line 1 - Downtown Loop");
        assertThat(result.get(1).getName()).isEqualTo("Line 2");
    }

    @Test
    void findAll_emptyList() {
        when(busLineRepo.findAll()).thenReturn(List.of());
        assertThat(service.findAll()).isEmpty();
    }

    // ===== findById =====

    @Test
    void findById_existingId_returnsDto() {
        when(busLineRepo.findById(1L)).thenReturn(Optional.of(sampleLine));

        BusLineResponseDTO dto = service.findById(1L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Line 1 - Downtown Loop");
        assertThat(dto.getActive()).isTrue();
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(busLineRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ===== create =====

    @Test
    void create_validDto_returnsCreatedLine() {
        BusLineRequestDTO req = new BusLineRequestDTO();
        req.setName("New Line");
        req.setDescription("Test description");

        when(busLineRepo.save(any(BusLine.class))).thenAnswer(inv -> {
            BusLine saved = inv.getArgument(0);
            saved.setId(5L);
            saved.setRouteStops(new ArrayList<>());
            return saved;
        });

        BusLineResponseDTO result = service.create(req);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("New Line");
        assertThat(result.getDescription()).isEqualTo("Test description");
        verify(busLineRepo).save(any(BusLine.class));
    }

    // ===== update =====

    @Test
    void update_existingId_updatesAndReturns() {
        BusLineRequestDTO req = new BusLineRequestDTO();
        req.setName("Updated Name");
        req.setDescription("Updated desc");

        when(busLineRepo.findById(1L)).thenReturn(Optional.of(sampleLine));
        when(busLineRepo.save(any(BusLine.class))).thenAnswer(inv -> inv.getArgument(0));

        BusLineResponseDTO result = service.update(1L, req);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getDescription()).isEqualTo("Updated desc");
    }

    @Test
    void update_nonExistingId_throwsException() {
        BusLineRequestDTO req = new BusLineRequestDTO();
        req.setName("X");
        when(busLineRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ===== delete =====

    @Test
    void delete_noActiveTickets_hardDeletes() {
        when(busLineRepo.findById(1L)).thenReturn(Optional.of(sampleLine));
        when(ticketRepo.existsByBusLineIdAndActiveTrue(1L)).thenReturn(false);

        service.delete(1L);

        verify(busLineRepo).delete(sampleLine);
        verify(busLineRepo, never()).save(any());
    }

    @Test
    void delete_hasActiveTickets_softDeletes() {
        when(busLineRepo.findById(1L)).thenReturn(Optional.of(sampleLine));
        when(ticketRepo.existsByBusLineIdAndActiveTrue(1L)).thenReturn(true);

        service.delete(1L);

        assertThat(sampleLine.getActive()).isFalse();
        verify(busLineRepo).save(sampleLine);
        verify(busLineRepo, never()).delete(any());
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(busLineRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ===== setRoute =====

    @Test
    void setRoute_validStops_replacesRoute() {
        RouteRequestDTO req = new RouteRequestDTO();
        req.setStopIds(List.of(1L, 2L));

        BusStop stop2 = new BusStop();
        stop2.setId(2L);
        stop2.setName("City Hall");

        when(busLineRepo.findById(1L)).thenReturn(Optional.of(sampleLine));
        when(busStopRepo.findById(1L)).thenReturn(Optional.of(sampleStop));
        when(busStopRepo.findById(2L)).thenReturn(Optional.of(stop2));
        when(busLineRepo.save(any(BusLine.class))).thenAnswer(inv -> inv.getArgument(0));

        BusLineResponseDTO result = service.setRoute(1L, req);

        assertThat(result.getStops()).hasSize(2);
        assertThat(result.getStops().get(0).getStopName()).isEqualTo("Central Station");
        assertThat(result.getStops().get(0).getStopOrder()).isEqualTo(1);
        assertThat(result.getStops().get(1).getStopName()).isEqualTo("City Hall");
        assertThat(result.getStops().get(1).getStopOrder()).isEqualTo(2);
    }

    @Test
    void setRoute_invalidStopId_throwsException() {
        RouteRequestDTO req = new RouteRequestDTO();
        req.setStopIds(List.of(1L, 999L));

        when(busLineRepo.findById(1L)).thenReturn(Optional.of(sampleLine));
        when(busStopRepo.findById(1L)).thenReturn(Optional.of(sampleStop));
        when(busStopRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setRoute(1L, req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void setRoute_invalidLineId_throwsException() {
        RouteRequestDTO req = new RouteRequestDTO();
        req.setStopIds(List.of(1L));
        when(busLineRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setRoute(99L, req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ===== getRoute =====

    @Test
    void getRoute_existingLine_returnsOrderedStops() {
        RouteStop rs1 = new RouteStop();
        rs1.setStopOrder(1);
        rs1.setBusStop(sampleStop);

        BusStop stop2 = new BusStop();
        stop2.setId(2L);
        stop2.setName("City Hall");
        RouteStop rs2 = new RouteStop();
        rs2.setStopOrder(2);
        rs2.setBusStop(stop2);

        when(busLineRepo.existsById(1L)).thenReturn(true);
        when(routeStopRepo.findByBusLineIdOrderByStopOrderAsc(1L)).thenReturn(List.of(rs1, rs2));

        var result = service.getRoute(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStopOrder()).isEqualTo(1);
        assertThat(result.get(1).getStopOrder()).isEqualTo(2);
    }

    @Test
    void getRoute_nonExistingLine_throwsException() {
        when(busLineRepo.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.getRoute(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ===== DTO mapping =====

    @Test
    void findById_withRouteStops_mapsStopsCorrectly() {
        RouteStop rs = new RouteStop();
        rs.setStopOrder(1);
        rs.setBusStop(sampleStop);
        rs.setBusLine(sampleLine);
        sampleLine.getRouteStops().add(rs);

        when(busLineRepo.findById(1L)).thenReturn(Optional.of(sampleLine));

        BusLineResponseDTO dto = service.findById(1L);

        assertThat(dto.getStops()).hasSize(1);
        assertThat(dto.getStops().get(0).getStopId()).isEqualTo(1L);
        assertThat(dto.getStops().get(0).getStopName()).isEqualTo("Central Station");
    }
}