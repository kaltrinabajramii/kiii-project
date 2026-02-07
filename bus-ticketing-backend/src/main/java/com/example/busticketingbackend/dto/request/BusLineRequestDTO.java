package com.example.busticketingbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BusLineRequestDTO {

    @NotBlank(message = "Bus line name is required")
    @Size(max = 200, message = "Name must be under 200 characters")
    private String name;

    @Size(max = 500, message = "Description must be under 500 characters")
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
