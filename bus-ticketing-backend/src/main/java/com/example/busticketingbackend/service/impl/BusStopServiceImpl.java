package com.example.busticketingbackend.service.impl;

import com.example.busticketingbackend.dto.request.BusStopRequestDTO;
import com.example.busticketingbackend.dto.response.BusStopResponseDTO;
import com.example.busticketingbackend.model.BusStop;
import com.example.busticketingbackend.repository.BusStopRepository;
import com.example.busticketingbackend.exception.ResourceNotFoundException;
import com.example.busticketingbackend.service.BusStopService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusStopServiceImpl implements BusStopService {

    private final BusStopRepository repo;

    public BusStopServiceImpl(BusStopRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<BusStopResponseDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public BusStopResponseDTO create(BusStopRequestDTO dto) {
        BusStop stop = new BusStop();
        stop.setName(dto.getName());
        stop.setLatitude(dto.getLatitude());
        stop.setLongitude(dto.getLongitude());
        return toDto(repo.save(stop));
    }

    @Override
    public BusStopResponseDTO update(Long id, BusStopRequestDTO dto) {
        BusStop stop = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus stop not found with id: " + id));
        stop.setName(dto.getName());
        stop.setLatitude(dto.getLatitude());
        stop.setLongitude(dto.getLongitude());
        return toDto(repo.save(stop));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Bus stop not found with id: " + id);
        }
        repo.deleteById(id);
    }

    private BusStopResponseDTO toDto(BusStop stop) {
        BusStopResponseDTO dto = new BusStopResponseDTO();
        dto.setId(stop.getId());
        dto.setName(stop.getName());
        dto.setLatitude(stop.getLatitude());
        dto.setLongitude(stop.getLongitude());
        return dto;
    }
}
