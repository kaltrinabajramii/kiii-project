package com.example.busticketingbackend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class TicketPurchaseRequestDTO {

    @NotBlank(message = "Passenger name is required")
    private String passengerName;

    @NotBlank(message = "Passenger email is required")
    @Email(message = "Invalid email format")
    private String passengerEmail;

    @NotNull(message = "Ticket category is required")
    private Long ticketCategoryId;

    private Long busLineId;

    private LocalDateTime validFrom;

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getPassengerEmail() { return passengerEmail; }
    public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }
    public Long getTicketCategoryId() { return ticketCategoryId; }
    public void setTicketCategoryId(Long ticketCategoryId) { this.ticketCategoryId = ticketCategoryId; }
    public Long getBusLineId() { return busLineId; }
    public void setBusLineId(Long busLineId) { this.busLineId = busLineId; }
    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }
}
