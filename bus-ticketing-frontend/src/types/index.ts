export interface BusStop {
    id: number;
    name: string;
    latitude: number | null;
    longitude: number | null;
}

export interface RouteStop {
    stopOrder: number;
    stopId: number;
    stopName: string;
}

export interface BusLine {
    id: number;
    name: string;
    description: string | null;
    active: boolean;
    stops: RouteStop[];
}

export interface TicketCategory {
    id: number;
    name: string;
    durationDays: number;
    price: number;
    description: string | null;
}

export interface Ticket {
    id: number;
    passengerName: string;
    passengerEmail: string;
    categoryName: string;
    busLineName: string | null;
    purchaseDate: string;
    validFrom: string;
    expirationDate: string;
    active: boolean;
    expired: boolean;
    daysRemaining: number;
}

export interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
}