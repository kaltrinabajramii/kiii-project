package com.example.busticketingbackend.repository;

import com.example.busticketingbackend.model.BusLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusLineRepository extends JpaRepository<BusLine, Long> {
}


