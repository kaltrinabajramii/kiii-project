package com.example.busticketingbackend.repository;

import com.example.busticketingbackend.model.TicketCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketCategoryRepository extends JpaRepository<TicketCategory, Long> {
}
