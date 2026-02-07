package com.example.busticketingbackend.repository;

import com.example.busticketingbackend.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t FROM Ticket t WHERE " +
            "(:email IS NULL OR t.passengerEmail = :email) AND " +
            "(:active IS NULL OR t.active = :active) AND " +
            "(:busLineId IS NULL OR t.busLine.id = :busLineId)")
    Page<Ticket> findFiltered(
            @Param("email") String email,
            @Param("active") Boolean active,
            @Param("busLineId") Long busLineId,
            Pageable pageable);

    boolean existsByBusLineIdAndActiveTrue(Long busLineId);
}