package com.example.busticketingbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BusStopRequestDTO {

    @NotBlank(message = "Stop name is required")
    @Size(max = 150, message = "Name must be under 150 characters")
    private String name;

    private Double latitude;
    private Double longitude;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
