package com.example.busticketingbackend.config;

import com.example.busticketingbackend.model.*;
import com.example.busticketingbackend.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder {

    private final BusStopRepository busStopRepo;
    private final BusLineRepository busLineRepo;
    private final TicketCategoryRepository categoryRepo;
    private final TicketRepository ticketRepo;

    public DataSeeder(BusStopRepository busStopRepo, BusLineRepository busLineRepo,
                      TicketCategoryRepository categoryRepo, TicketRepository ticketRepo) {
        this.busStopRepo = busStopRepo;
        this.busLineRepo = busLineRepo;
        this.categoryRepo = categoryRepo;
        this.ticketRepo = ticketRepo;
    }

    @PostConstruct
    public void seed() {
        if (busStopRepo.count() > 0) return;

        // ── Bus Stops ──
        BusStop centralStation   = saveStop("Central Station", 41.9973, 21.4280);
        BusStop cityHall         = saveStop("City Hall", 41.9960, 21.4315);
        BusStop oldMarket        = saveStop("Old Market Square", 41.9945, 21.4350);
        BusStop riverside        = saveStop("Riverside Park", 41.9930, 21.4250);
        BusStop university       = saveStop("University Campus", 41.9990, 21.4200);
        BusStop businessDistrict = saveStop("Business District", 42.0010, 21.4350);
        BusStop shoppingMall     = saveStop("Shopping Mall", 42.0030, 21.4400);
        BusStop suburbNorth      = saveStop("Suburb North", 42.0100, 21.4450);
        BusStop airport          = saveStop("Airport Terminal", 42.0200, 21.4500);
        BusStop westIndustrial   = saveStop("West Industrial Zone", 41.9900, 21.4100);
        BusStop hospital         = saveStop("Hospital", 41.9955, 21.4220);
        BusStop sportsArena      = saveStop("Sports Arena", 41.9980, 21.4380);
        BusStop eastResidential  = saveStop("East Residential", 42.0000, 21.4450);
        BusStop techPark         = saveStop("Tech Park", 42.0020, 21.4500);
        BusStop eastTerminal     = saveStop("East Terminal", 42.0050, 21.4550);
        BusStop trainStation     = saveStop("Train Station", 41.9965, 21.4260);
        BusStop museum           = saveStop("National Museum", 41.9950, 21.4290);
        BusStop stadium          = saveStop("City Stadium", 42.0040, 21.4320);
        BusStop zoo              = saveStop("Zoo & Botanical Garden", 42.0060, 21.4380);
        BusStop southGate        = saveStop("South Gate", 41.9880, 21.4300);
        BusStop lakeshore        = saveStop("Lakeshore Promenade", 41.9860, 21.4340);
        BusStop marketSquare     = saveStop("Farmers Market", 41.9940, 21.4180);
        BusStop theater          = saveStop("National Theater", 41.9958, 21.4330);
        BusStop parkRidge        = saveStop("Park Ridge", 42.0080, 21.4260);
        BusStop northTerminal    = saveStop("North Terminal", 42.0150, 21.4300);

        // ── Bus Lines ──
        BusLine line1 = saveLine("Line 1 - Downtown Loop", "Circular route through downtown");
        addRoute(line1, List.of(centralStation, cityHall, oldMarket, riverside, university, centralStation));

        BusLine line2 = saveLine("Line 2 - Airport Express", "Direct service to airport");
        addRoute(line2, List.of(centralStation, businessDistrict, shoppingMall, suburbNorth, airport));

        BusLine line3 = saveLine("Line 3 - East-West Connector", "Cross-city east to west route");
        addRoute(line3, List.of(westIndustrial, hospital, cityHall, sportsArena, eastResidential, techPark, eastTerminal));

        BusLine line4 = saveLine("Line 4 - Cultural Route", "Museums, theaters & landmarks");
        addRoute(line4, List.of(trainStation, museum, theater, cityHall, oldMarket, lakeshore));

        BusLine line5 = saveLine("Line 5 - North Express", "Express service to northern suburbs");
        addRoute(line5, List.of(centralStation, university, parkRidge, suburbNorth, northTerminal));

        BusLine line6 = saveLine("Line 6 - Southern Ring", "Southern neighborhoods loop");
        addRoute(line6, List.of(centralStation, riverside, southGate, lakeshore, marketSquare, hospital, centralStation));

        BusLine line7 = saveLine("Line 7 - Stadium Shuttle", "Game-day shuttle service");
        addRoute(line7, List.of(centralStation, businessDistrict, stadium, zoo, shoppingMall));

        BusLine line8 = saveLine("Line 8 - Tech Corridor", "Service to tech & business parks");
        addRoute(line8, List.of(university, techPark, businessDistrict, stadium, eastTerminal));

        // ── Ticket Categories ──
        TicketCategory singleRide    = saveCategory("Single Ride", 0, "1.50", "Valid for one trip");
        TicketCategory dayPass       = saveCategory("Day Pass", 1, "4.00", "Unlimited rides for 24h");
        TicketCategory weeklyPass    = saveCategory("Weekly Pass", 7, "15.00", "Valid for 7 days");
        TicketCategory monthlyPass   = saveCategory("Monthly Pass", 30, "45.00", "Valid for 30 days");
        TicketCategory studentMonthly = saveCategory("Student Monthly", 30, "25.00", "Requires student ID");
        TicketCategory seniorMonthly = saveCategory("Senior Monthly", 30, "20.00", "For passengers 65+");
        TicketCategory yearlyPass    = saveCategory("Yearly Pass", 365, "400.00", "Best value");
        TicketCategory weekendPass   = saveCategory("Weekend Pass", 2, "6.00", "Valid Saturday & Sunday");
        TicketCategory touristPass   = saveCategory("Tourist 3-Day", 3, "10.00", "Perfect for visitors");

        // ── Tickets ──
        LocalDateTime now = LocalDateTime.now();

        // Active monthly passes
        saveTicket("Elena Petrova", "elena@example.com", monthlyPass, line1,
                now.minusDays(10), now.minusDays(10), now.plusDays(20));
        saveTicket("Marko Nikolov", "marko@example.com", monthlyPass, null,
                now.minusDays(5), now.minusDays(5), now.plusDays(25));
        saveTicket("Ana Stojanova", "ana.s@example.com", monthlyPass, line3,
                now.minusDays(15), now.minusDays(15), now.plusDays(15));

        // Expiring soon (within 7 days)
        saveTicket("Stefan Dimitrov", "stefan@example.com", weeklyPass, line2,
                now.minusDays(5), now.minusDays(5), now.plusDays(2));
        saveTicket("Ivana Trajkova", "ivana@example.com", weeklyPass, line1,
                now.minusDays(4), now.minusDays(4), now.plusDays(3));
        saveTicket("Petar Georgiev", "petar@example.com", dayPass, line4,
                now.minusHours(18), now.minusHours(18), now.plusHours(6));

        // Already expired
        saveTicket("Maria Kosta", "maria.k@example.com", weeklyPass, line1,
                now.minusDays(14), now.minusDays(14), now.minusDays(7));
        saveTicket("Nikola Popov", "nikola@example.com", monthlyPass, line2,
                now.minusDays(60), now.minusDays(60), now.minusDays(30));
        saveTicket("Sofija Ilievska", "sofija@example.com", dayPass, line3,
                now.minusDays(3), now.minusDays(3), now.minusDays(2));

        // Student passes
        saveTicket("Aleksandar Mitrev", "alex.m@university.edu", studentMonthly, line5,
                now.minusDays(8), now.minusDays(8), now.plusDays(22));
        saveTicket("Katerina Jovanova", "kate.j@university.edu", studentMonthly, null,
                now.minusDays(20), now.minusDays(20), now.plusDays(10));
        saveTicket("Bojan Ristov", "bojan.r@university.edu", studentMonthly, line8,
                now.minusDays(28), now.minusDays(28), now.plusDays(2));

        // Senior passes
        saveTicket("Gordan Trajkov", "gordan.t@example.com", seniorMonthly, line6,
                now.minusDays(12), now.minusDays(12), now.plusDays(18));
        saveTicket("Blagica Dimova", "blagica@example.com", seniorMonthly, line1,
                now.minusDays(25), now.minusDays(25), now.plusDays(5));

        // Single ride tickets (expired after 2h)
        saveTicket("Tourist Mike", "mike@tourist.com", singleRide, line2,
                now.minusHours(5), now.minusHours(5), now.minusHours(3));
        saveTicket("Tourist Sarah", "sarah@tourist.com", singleRide, line4,
                now.minusMinutes(30), now.minusMinutes(30), now.plusMinutes(90));

        // Tourist passes
        saveTicket("James Wilson", "james.w@travel.com", touristPass, null,
                now.minusDays(1), now.minusDays(1), now.plusDays(2));
        saveTicket("Yuki Tanaka", "yuki@travel.jp", touristPass, null,
                now, now, now.plusDays(3));

        // Yearly pass
        saveTicket("Viktor Angelov", "viktor@example.com", yearlyPass, null,
                now.minusDays(100), now.minusDays(100), now.plusDays(265));

        // Weekend passes
        saveTicket("Darko Lazarev", "darko@example.com", weekendPass, line7,
                now.minusDays(1), now.minusDays(1), now.plusDays(1));

        // Cancelled ticket
        Ticket cancelled = saveTicket("Filip Stojanovski", "filip@example.com", monthlyPass, line1,
                now.minusDays(20), now.minusDays(20), now.plusDays(10));
        cancelled.setActive(false);
        ticketRepo.save(cancelled);

        // Multiple tickets for same person (history)
        saveTicket("Elena Petrova", "elena@example.com", weeklyPass, line4,
                now.minusDays(40), now.minusDays(40), now.minusDays(33));
        saveTicket("Elena Petrova", "elena@example.com", dayPass, line7,
                now.minusDays(50), now.minusDays(50), now.minusDays(49));
    }

    private BusStop saveStop(String name, double lat, double lon) {
        BusStop s = new BusStop();
        s.setName(name);
        s.setLatitude(lat);
        s.setLongitude(lon);
        return busStopRepo.save(s);
    }

    private BusLine saveLine(String name, String desc) {
        BusLine l = new BusLine();
        l.setName(name);
        l.setDescription(desc);
        return busLineRepo.save(l);
    }

    private void addRoute(BusLine line, List<BusStop> stops) {
        for (int i = 0; i < stops.size(); i++) {
            RouteStop rs = new RouteStop();
            rs.setBusLine(line);
            rs.setBusStop(stops.get(i));
            rs.setStopOrder(i + 1);
            line.getRouteStops().add(rs);
        }
        busLineRepo.save(line);
    }

    private TicketCategory saveCategory(String name, int days, String price, String desc) {
        TicketCategory c = new TicketCategory();
        c.setName(name);
        c.setDurationDays(days);
        c.setPrice(new BigDecimal(price));
        c.setDescription(desc);
        return categoryRepo.save(c);
    }

    private Ticket saveTicket(String name, String email, TicketCategory category,
                              BusLine busLine, LocalDateTime purchased,
                              LocalDateTime validFrom, LocalDateTime expiration) {
        Ticket t = new Ticket();
        t.setPassengerName(name);
        t.setPassengerEmail(email);
        t.setTicketCategory(category);
        t.setBusLine(busLine);
        t.setPurchaseDate(purchased);
        t.setValidFrom(validFrom);
        t.setExpirationDate(expiration);
        t.setActive(true);
        return ticketRepo.save(t);
    }
}