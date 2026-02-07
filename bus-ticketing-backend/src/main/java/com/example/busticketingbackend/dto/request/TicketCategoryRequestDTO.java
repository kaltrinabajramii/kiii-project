package com.example.busticketingbackend.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class TicketCategoryRequestDTO {

    @NotBlank(message = "Category name is required")
    private String name;

    @NotNull(message = "Duration days is required")
    @Min(value = 0, message = "Duration days must be >= 0")
    private Integer durationDays;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price must be >= 0")
    private BigDecimal price;

    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
