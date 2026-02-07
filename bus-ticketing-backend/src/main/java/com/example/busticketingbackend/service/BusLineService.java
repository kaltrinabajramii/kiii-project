package com.example.busticketingbackend.service;

import com.example.busticketingbackend.dto.request.BusLineRequestDTO;
import com.example.busticketingbackend.dto.request.RouteRequestDTO;
import com.example.busticketingbackend.dto.response.BusLineResponseDTO;
import com.example.busticketingbackend.dto.response.BusLineResponseDTO.RouteStopDTO;

import java.util.List;

public interface BusLineService {
    List<BusLineResponseDTO> findAll();
    BusLineResponseDTO findById(Long id);
    BusLineResponseDTO create(BusLineRequestDTO dto);
    BusLineResponseDTO update(Long id, BusLineRequestDTO dto);
    void delete(Long id);
    BusLineResponseDTO setRoute(Long lineId, RouteRequestDTO dto);
    List<RouteStopDTO> getRoute(Long lineId);
}

