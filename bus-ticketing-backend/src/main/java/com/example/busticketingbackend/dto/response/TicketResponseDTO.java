package com.example.busticketingbackend.dto.response;

import java.time.LocalDateTime;

public class TicketResponseDTO {

    private Long id;
    private String passengerName;
    private String passengerEmail;
    private String categoryName;
    private String busLineName;
    private LocalDateTime purchaseDate;
    private LocalDateTime validFrom;
    private LocalDateTime expirationDate;
    private Boolean active;
    private Boolean expired;
    private Long daysRemaining;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getPassengerEmail() { return passengerEmail; }
    public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getBusLineName() { return busLineName; }
    public void setBusLineName(String busLineName) { this.busLineName = busLineName; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }
    public LocalDateTime getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Boolean getExpired() { return expired; }
    public void setExpired(Boolean expired) { this.expired = expired; }
    public Long getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(Long daysRemaining) { this.daysRemaining = daysRemaining; }
}