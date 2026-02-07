package com.example.busticketingbackend.service;

import com.example.busticketingbackend.dto.request.BusStopRequestDTO;
import com.example.busticketingbackend.dto.response.BusStopResponseDTO;

import java.util.List;

public interface BusStopService {
    List<BusStopResponseDTO> findAll();
    BusStopResponseDTO create(BusStopRequestDTO dto);
    BusStopResponseDTO update(Long id, BusStopRequestDTO dto);
    void delete(Long id);
}