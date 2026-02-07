package com.example.busticketingbackend.repository;

import com.example.busticketingbackend.model.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {
    List<RouteStop> findByBusLineIdOrderByStopOrderAsc(Long busLineId);
    void deleteByBusLineId(Long busLineId);
}