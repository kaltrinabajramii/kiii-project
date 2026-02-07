package com.example.busticketingbackend.dto.response;

import java.util.List;

public class BusLineResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private List<RouteStopDTO> stops;

    public static class RouteStopDTO {
        private Integer stopOrder;
        private Long stopId;
        private String stopName;

        public RouteStopDTO() {}
        public RouteStopDTO(Integer stopOrder, Long stopId, String stopName) {
            this.stopOrder = stopOrder;
            this.stopId = stopId;
            this.stopName = stopName;
        }

        public Integer getStopOrder() { return stopOrder; }
        public void setStopOrder(Integer stopOrder) { this.stopOrder = stopOrder; }
        public Long getStopId() { return stopId; }
        public void setStopId(Long stopId) { this.stopId = stopId; }
        public String getStopName() { return stopName; }
        public void setStopName(String stopName) { this.stopName = stopName; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public List<RouteStopDTO> getStops() { return stops; }
    public void setStops(List<RouteStopDTO> stops) { this.stops = stops; }
}
