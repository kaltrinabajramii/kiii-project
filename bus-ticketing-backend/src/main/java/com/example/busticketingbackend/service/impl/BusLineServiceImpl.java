package com.example.busticketingbackend.service.impl;

import com.example.busticketingbackend.dto.request.BusLineRequestDTO;
import com.example.busticketingbackend.dto.request.RouteRequestDTO;
import com.example.busticketingbackend.dto.response.BusLineResponseDTO;
import com.example.busticketingbackend.dto.response.BusLineResponseDTO.RouteStopDTO;
import com.example.busticketingbackend.model.BusLine;
import com.example.busticketingbackend.model.BusStop;
import com.example.busticketingbackend.model.RouteStop;
import com.example.busticketingbackend.repository.BusLineRepository;
import com.example.busticketingbackend.repository.BusStopRepository;
import com.example.busticketingbackend.repository.RouteStopRepository;
import com.example.busticketingbackend.repository.TicketRepository;
import com.example.busticketingbackend.exception.ResourceNotFoundException;
import com.example.busticketingbackend.service.BusLineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusLineServiceImpl implements BusLineService {

    private final BusLineRepository busLineRepo;
    private final BusStopRepository busStopRepo;
    private final RouteStopRepository routeStopRepo;
    private final TicketRepository ticketRepo;

    public BusLineServiceImpl(BusLineRepository busLineRepo, BusStopRepository busStopRepo,
                              RouteStopRepository routeStopRepo, TicketRepository ticketRepo) {
        this.busLineRepo = busLineRepo;
        this.busStopRepo = busStopRepo;
        this.routeStopRepo = routeStopRepo;
        this.ticketRepo = ticketRepo;
    }

    @Override
    public List<BusLineResponseDTO> findAll() {
        return busLineRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public BusLineResponseDTO findById(Long id) {
        return toDto(busLineRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus line not found with id: " + id)));
    }

    @Override
    public BusLineResponseDTO create(BusLineRequestDTO dto) {
        BusLine line = new BusLine();
        line.setName(dto.getName());
        line.setDescription(dto.getDescription());
        return toDto(busLineRepo.save(line));
    }

    @Override
    public BusLineResponseDTO update(Long id, BusLineRequestDTO dto) {
        BusLine line = busLineRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus line not found with id: " + id));
        line.setName(dto.getName());
        line.setDescription(dto.getDescription());
        return toDto(busLineRepo.save(line));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        BusLine line = busLineRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus line not found with id: " + id));
        if (ticketRepo.existsByBusLineIdAndActiveTrue(id)) {
            line.setActive(false);
            busLineRepo.save(line);
        } else {
            busLineRepo.delete(line);
        }
    }

    @Override
    @Transactional
    public BusLineResponseDTO setRoute(Long lineId, RouteRequestDTO dto) {
        BusLine line = busLineRepo.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus line not found with id: " + lineId));

        line.getRouteStops().clear();
        busLineRepo.flush();

        List<RouteStop> newStops = new ArrayList<>();
        for (int i = 0; i < dto.getStopIds().size(); i++) {
            Long stopId = dto.getStopIds().get(i);
            BusStop stop = busStopRepo.findById(stopId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bus stop not found with id: " + stopId));
            RouteStop rs = new RouteStop();
            rs.setBusLine(line);
            rs.setBusStop(stop);
            rs.setStopOrder(i + 1);
            newStops.add(rs);
        }
        line.getRouteStops().addAll(newStops);
        return toDto(busLineRepo.save(line));
    }

    @Override
    public List<RouteStopDTO> getRoute(Long lineId) {
        if (!busLineRepo.existsById(lineId)) {
            throw new ResourceNotFoundException("Bus line not found with id: " + lineId);
        }
        return routeStopRepo.findByBusLineIdOrderByStopOrderAsc(lineId).stream()
                .map(rs -> new RouteStopDTO(rs.getStopOrder(), rs.getBusStop().getId(), rs.getBusStop().getName()))
                .collect(Collectors.toList());
    }

    private BusLineResponseDTO toDto(BusLine line) {
        BusLineResponseDTO dto = new BusLineResponseDTO();
        dto.setId(line.getId());
        dto.setName(line.getName());
        dto.setDescription(line.getDescription());
        dto.setActive(line.getActive());
        dto.setStops(line.getRouteStops().stream()
                .map(rs -> new RouteStopDTO(rs.getStopOrder(), rs.getBusStop().getId(), rs.getBusStop().getName()))
                .collect(Collectors.toList()));
        return dto;
    }
}