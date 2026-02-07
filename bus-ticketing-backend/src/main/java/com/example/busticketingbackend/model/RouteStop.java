package com.example.busticketingbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "route_stops")
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_line_id", nullable = false)
    private BusLine busLine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bus_stop_id", nullable = false)
    private BusStop busStop;

    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BusLine getBusLine() { return busLine; }
    public void setBusLine(BusLine busLine) { this.busLine = busLine; }
    public BusStop getBusStop() { return busStop; }
    public void setBusStop(BusStop busStop) { this.busStop = busStop; }
    public Integer getStopOrder() { return stopOrder; }
    public void setStopOrder(Integer stopOrder) { this.stopOrder = stopOrder; }
}