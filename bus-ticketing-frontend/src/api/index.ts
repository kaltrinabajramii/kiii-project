import type { BusLine, BusStop, TicketCategory, Ticket, Page } from '../types';

const BASE = '/api';

async function request<T>(url: string, opts?: RequestInit): Promise<T> {
    const res = await fetch(BASE + url, {
        headers: { 'Content-Type': 'application/json' },
        ...opts,
    });
    if (!res.ok) {
        const err = await res.json().catch(() => ({ message: res.statusText }));
        throw new Error(err.message || res.statusText);
    }
    if (res.status === 204) return undefined as T;
    return res.json();
}

// Bus Lines
export const busLineApi = {
    getAll: () => request<BusLine[]>('/bus-lines'),
    getById: (id: number) => request<BusLine>(`/bus-lines/${id}`),
    create: (data: { name: string; description?: string }) =>
        request<BusLine>('/bus-lines', { method: 'POST', body: JSON.stringify(data) }),
    update: (id: number, data: { name: string; description?: string }) =>
        request<BusLine>(`/bus-lines/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id: number) =>
        request<void>(`/bus-lines/${id}`, { method: 'DELETE' }),
    setRoute: (id: number, stopIds: number[]) =>
        request<BusLine>(`/bus-lines/${id}/route`, { method: 'PUT', body: JSON.stringify({ stopIds }) }),
};

// Bus Stops
export const busStopApi = {
    getAll: () => request<BusStop[]>('/bus-stops'),
    create: (data: { name: string; latitude?: number; longitude?: number }) =>
        request<BusStop>('/bus-stops', { method: 'POST', body: JSON.stringify(data) }),
    update: (id: number, data: { name: string; latitude?: number; longitude?: number }) =>
        request<BusStop>(`/bus-stops/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id: number) =>
        request<void>(`/bus-stops/${id}`, { method: 'DELETE' }),
};

// Ticket Categories
export const categoryApi = {
    getAll: () => request<TicketCategory[]>('/ticket-categories'),
    create: (data: { name: string; durationDays: number; price: number; description?: string }) =>
        request<TicketCategory>('/ticket-categories', { method: 'POST', body: JSON.stringify(data) }),
    update: (id: number, data: { name: string; durationDays: number; price: number; description?: string }) =>
        request<TicketCategory>(`/ticket-categories/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id: number) =>
        request<void>(`/ticket-categories/${id}`, { method: 'DELETE' }),
};

// Tickets
export const ticketApi = {
    getAll: (params: { email?: string; active?: boolean; busLineId?: number; page?: number; size?: number }) => {
        const q = new URLSearchParams();
        if (params.email) q.set('email', params.email);
        if (params.active !== undefined) q.set('active', String(params.active));
        if (params.busLineId) q.set('busLineId', String(params.busLineId));
        q.set('page', String(params.page ?? 0));
        q.set('size', String(params.size ?? 20));
        q.set('sort', 'purchaseDate,desc');
        return request<Page<Ticket>>(`/tickets?${q}`);
    },
    getById: (id: number) => request<Ticket>(`/tickets/${id}`),
    purchase: (data: {
        passengerName: string;
        passengerEmail: string;
        ticketCategoryId: number;
        busLineId?: number | null;
        validFrom?: string;
    }) => request<Ticket>('/tickets', { method: 'POST', body: JSON.stringify(data) }),
    renew: (id: number) => request<Ticket>(`/tickets/${id}/renew`, { method: 'POST' }),
    cancel: (id: number) => request<void>(`/tickets/${id}`, { method: 'DELETE' }),
};