package com.example.busticketingbackend.service.impl;

import com.example.busticketingbackend.dto.request.BusStopRequestDTO;
import com.example.busticketingbackend.dto.response.BusStopResponseDTO;
import com.example.busticketingbackend.exception.ResourceNotFoundException;
import com.example.busticketingbackend.model.BusStop;
import com.example.busticketingbackend.repository.BusStopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusStopServiceImplTest {

    @Mock private BusStopRepository repo;
    @InjectMocks private BusStopServiceImpl service;

    private BusStop sampleStop;

    @BeforeEach
    void setUp() {
        sampleStop = new BusStop();
        sampleStop.setId(1L);
        sampleStop.setName("Central Station");
        sampleStop.setLatitude(41.9973);
        sampleStop.setLongitude(21.4280);
    }

    @Test
    void findAll_returnsMappedDtos() {
        BusStop stop2 = new BusStop();
        stop2.setId(2L);
        stop2.setName("City Hall");
        stop2.setLatitude(41.996);
        stop2.setLongitude(21.431);

        when(repo.findAll()).thenReturn(List.of(sampleStop, stop2));

        List<BusStopResponseDTO> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Central Station");
        assertThat(result.get(0).getLatitude()).isEqualTo(41.9973);
        assertThat(result.get(1).getName()).isEqualTo("City Hall");
    }

    @Test
    void findAll_empty() {
        when(repo.findAll()).thenReturn(List.of());
        assertThat(service.findAll()).isEmpty();
    }

    @Test
    void create_validDto_savesAndReturns() {
        BusStopRequestDTO req = new BusStopRequestDTO();
        req.setName("New Stop");
        req.setLatitude(42.0);
        req.setLongitude(21.5);

        when(repo.save(any(BusStop.class))).thenAnswer(inv -> {
            BusStop s = inv.getArgument(0);
            s.setId(10L);
            return s;
        });

        BusStopResponseDTO result = service.create(req);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("New Stop");
        assertThat(result.getLatitude()).isEqualTo(42.0);
        assertThat(result.getLongitude()).isEqualTo(21.5);
    }

    @Test
    void create_nullCoordinates_savesSuccessfully() {
        BusStopRequestDTO req = new BusStopRequestDTO();
        req.setName("Stop Without Coords");

        when(repo.save(any(BusStop.class))).thenAnswer(inv -> {
            BusStop s = inv.getArgument(0);
            s.setId(11L);
            return s;
        });

        BusStopResponseDTO result = service.create(req);

        assertThat(result.getLatitude()).isNull();
        assertThat(result.getLongitude()).isNull();
    }

    @Test
    void update_existingId_updatesAllFields() {
        BusStopRequestDTO req = new BusStopRequestDTO();
        req.setName("Updated Name");
        req.setLatitude(42.5);
        req.setLongitude(22.0);

        when(repo.findById(1L)).thenReturn(Optional.of(sampleStop));
        when(repo.save(any(BusStop.class))).thenAnswer(inv -> inv.getArgument(0));

        BusStopResponseDTO result = service.update(1L, req);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getLatitude()).isEqualTo(42.5);
        assertThat(result.getLongitude()).isEqualTo(22.0);
    }

    @Test
    void update_nonExistingId_throwsException() {
        BusStopRequestDTO req = new BusStopRequestDTO();
        req.setName("X");
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(repo.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(repo.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}