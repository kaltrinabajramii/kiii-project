package com.example.busticketingbackend.repository;

import com.example.busticketingbackend.model.BusStop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusStopRepository extends JpaRepository<BusStop, Long> {
}
