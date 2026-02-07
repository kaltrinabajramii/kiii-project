package com.example.busticketingbackend.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class RouteRequestDTO {

    @NotEmpty(message = "Route must have at least one stop")
    private List<Long> stopIds;

    public List<Long> getStopIds() { return stopIds; }
    public void setStopIds(List<Long> stopIds) { this.stopIds = stopIds; }
}