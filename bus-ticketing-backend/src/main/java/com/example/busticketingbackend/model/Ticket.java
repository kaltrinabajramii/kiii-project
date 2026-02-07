package com.example.busticketingbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "passenger_name", nullable = false)
    private String passengerName;

    @Column(name = "passenger_email", nullable = false)
    private String passengerEmail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_category_id", nullable = false)
    private TicketCategory ticketCategory;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bus_line_id")
    private BusLine busLine;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private Boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getPassengerEmail() { return passengerEmail; }
    public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }
    public TicketCategory getTicketCategory() { return ticketCategory; }
    public void setTicketCategory(TicketCategory ticketCategory) { this.ticketCategory = ticketCategory; }
    public BusLine getBusLine() { return busLine; }
    public void setBusLine(BusLine busLine) { this.busLine = busLine; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }
    public LocalDateTime getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}