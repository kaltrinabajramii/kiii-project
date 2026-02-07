package com.example.busticketingbackend.service.impl;

import com.example.busticketingbackend.dto.request.TicketCategoryRequestDTO;
import com.example.busticketingbackend.dto.response.TicketCategoryResponseDTO;
import com.example.busticketingbackend.exception.ResourceNotFoundException;
import com.example.busticketingbackend.model.TicketCategory;
import com.example.busticketingbackend.repository.TicketCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketCategoryServiceImplTest {

    @Mock private TicketCategoryRepository repo;
    @InjectMocks private TicketCategoryServiceImpl service;

    private TicketCategory monthlyPass;

    @BeforeEach
    void setUp() {
        monthlyPass = new TicketCategory();
        monthlyPass.setId(1L);
        monthlyPass.setName("Monthly Pass");
        monthlyPass.setDurationDays(30);
        monthlyPass.setPrice(new BigDecimal("45.00"));
        monthlyPass.setDescription("Valid for 30 days");
    }

    @Test
    void findAll_returnsMappedDtos() {
        TicketCategory singleRide = new TicketCategory();
        singleRide.setId(2L);
        singleRide.setName("Single Ride");
        singleRide.setDurationDays(0);
        singleRide.setPrice(new BigDecimal("1.50"));

        when(repo.findAll()).thenReturn(List.of(monthlyPass, singleRide));

        List<TicketCategoryResponseDTO> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Monthly Pass");
        assertThat(result.get(0).getPrice()).isEqualByComparingTo(new BigDecimal("45.00"));
        assertThat(result.get(1).getDurationDays()).isEqualTo(0);
    }

    @Test
    void create_validDto_savesAndReturns() {
        TicketCategoryRequestDTO req = new TicketCategoryRequestDTO();
        req.setName("Weekly Pass");
        req.setDurationDays(7);
        req.setPrice(new BigDecimal("15.00"));
        req.setDescription("Valid for 7 days");

        when(repo.save(any(TicketCategory.class))).thenAnswer(inv -> {
            TicketCategory c = inv.getArgument(0);
            c.setId(3L);
            return c;
        });

        TicketCategoryResponseDTO result = service.create(req);

        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("Weekly Pass");
        assertThat(result.getDurationDays()).isEqualTo(7);
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("15.00"));
    }

    @Test
    void update_existingId_updatesAllFields() {
        TicketCategoryRequestDTO req = new TicketCategoryRequestDTO();
        req.setName("Updated Monthly");
        req.setDurationDays(31);
        req.setPrice(new BigDecimal("50.00"));
        req.setDescription("Updated description");

        when(repo.findById(1L)).thenReturn(Optional.of(monthlyPass));
        when(repo.save(any(TicketCategory.class))).thenAnswer(inv -> inv.getArgument(0));

        TicketCategoryResponseDTO result = service.update(1L, req);

        assertThat(result.getName()).isEqualTo("Updated Monthly");
        assertThat(result.getDurationDays()).isEqualTo(31);
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void update_nonExistingId_throwsException() {
        TicketCategoryRequestDTO req = new TicketCategoryRequestDTO();
        req.setName("X");
        req.setDurationDays(1);
        req.setPrice(BigDecimal.ONE);
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_deletes() {
        when(repo.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(repo).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(repo.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}