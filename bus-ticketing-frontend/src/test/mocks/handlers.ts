import { http, HttpResponse } from 'msw';

// ===== Sample Data =====

export const mockBusStops = [
    { id: 1, name: 'Central Station', latitude: 41.9973, longitude: 21.4280 },
    { id: 2, name: 'City Hall', latitude: 41.9960, longitude: 21.4315 },
    { id: 3, name: 'Old Market Square', latitude: 41.9945, longitude: 21.4350 },
];

export const mockBusLines = [
    {
        id: 1, name: 'Line 1 - Downtown Loop', description: 'Circular route', active: true,
        stops: [
            { stopOrder: 1, stopId: 1, stopName: 'Central Station' },
            { stopOrder: 2, stopId: 2, stopName: 'City Hall' },
        ],
    },
    {
        id: 2, name: 'Line 2 - Airport Express', description: 'Direct to airport', active: true,
        stops: [{ stopOrder: 1, stopId: 1, stopName: 'Central Station' }],
    },
    {
        id: 3, name: 'Line 3 - Inactive', description: 'Decommissioned', active: false, stops: [],
    },
];

export const mockCategories = [
    { id: 1, name: 'Single Ride', durationDays: 0, price: 1.50, description: 'Valid for one trip' },
    { id: 2, name: 'Monthly Pass', durationDays: 30, price: 45.00, description: 'Valid for 30 days' },
    { id: 3, name: 'Weekly Pass', durationDays: 7, price: 15.00, description: 'Valid for 7 days' },
];

export const mockTickets = [
    {
        id: 1, passengerName: 'John Doe', passengerEmail: 'john@example.com',
        categoryName: 'Monthly Pass', busLineName: 'Line 1 - Downtown Loop',
        purchaseDate: '2026-02-01T10:00:00', validFrom: '2026-02-01T00:00:00',
        expirationDate: '2026-03-03T00:00:00', active: true, expired: false, daysRemaining: 25,
    },
    {
        id: 2, passengerName: 'Jane Smith', passengerEmail: 'jane@example.com',
        categoryName: 'Single Ride', busLineName: null,
        purchaseDate: '2026-02-05T14:00:00', validFrom: '2026-02-05T14:00:00',
        expirationDate: '2026-02-05T16:00:00', active: false, expired: true, daysRemaining: 0,
    },
    {
        id: 3, passengerName: 'John Doe', passengerEmail: 'john@example.com',
        categoryName: 'Weekly Pass', busLineName: null,
        purchaseDate: '2026-02-04T08:00:00', validFrom: '2026-02-04T00:00:00',
        expirationDate: '2026-02-11T00:00:00', active: true, expired: false, daysRemaining: 5,
    },
];

// ===== Handlers =====

export const handlers = [
    // Bus Stops
    http.get('/api/bus-stops', () => HttpResponse.json(mockBusStops)),

    http.post('/api/bus-stops', async ({ request }) => {
        const body = await request.json() as Record<string, unknown>;
        return HttpResponse.json({ id: 10, ...body }, { status: 201 });
    }),

    http.put('/api/bus-stops/:id', async ({ request, params }) => {
        const body = await request.json() as Record<string, unknown>;
        return HttpResponse.json({ id: Number(params.id), ...body });
    }),

    http.delete('/api/bus-stops/:id', () => new HttpResponse(null, { status: 204 })),

    // Bus Lines
    http.get('/api/bus-lines', () => HttpResponse.json(mockBusLines)),

    http.get('/api/bus-lines/:id', ({ params }) => {
        const line = mockBusLines.find(l => l.id === Number(params.id));
        return line ? HttpResponse.json(line) : HttpResponse.json({ message: 'Not found' }, { status: 404 });
    }),

    http.post('/api/bus-lines', async ({ request }) => {
        const body = await request.json() as Record<string, unknown>;
        return HttpResponse.json({ id: 10, active: true, stops: [], ...body }, { status: 201 });
    }),

    http.put('/api/bus-lines/:id', async ({ request, params }) => {
        const body = await request.json() as Record<string, unknown>;
        return HttpResponse.json({ id: Number(params.id), active: true, stops: [], ...body });
    }),

    http.delete('/api/bus-lines/:id', () => new HttpResponse(null, { status: 204 })),

    http.put('/api/bus-lines/:id/route', async ({ params }) => {
        const line = mockBusLines.find(l => l.id === Number(params.id));
        return HttpResponse.json(line);
    }),

    http.get('/api/bus-lines/:id/route', ({ params }) => {
        const line = mockBusLines.find(l => l.id === Number(params.id));
        return HttpResponse.json(line?.stops || []);
    }),

    // Ticket Categories
    http.get('/api/ticket-categories', () => HttpResponse.json(mockCategories)),

    http.post('/api/ticket-categories', async ({ request }) => {
        const body = await request.json() as Record<string, unknown>;
        return HttpResponse.json({ id: 10, ...body }, { status: 201 });
    }),

    http.put('/api/ticket-categories/:id', async ({ request, params }) => {
        const body = await request.json() as Record<string, unknown>;
        return HttpResponse.json({ id: Number(params.id), ...body });
    }),

    http.delete('/api/ticket-categories/:id', () => new HttpResponse(null, { status: 204 })),

    // Tickets
    http.get('/api/tickets', ({ request }) => {
        const url = new URL(request.url);
        const email = url.searchParams.get('email');
        let filtered = [...mockTickets];
        if (email) filtered = filtered.filter(t => t.passengerEmail === email);
        const active = url.searchParams.get('active');
        if (active !== null) filtered = filtered.filter(t => t.active === (active === 'true'));
        return HttpResponse.json({
            content: filtered,
            totalElements: filtered.length,
            totalPages: 1,
            number: 0,
            size: 20,
        });
    }),

    http.get('/api/tickets/:id', ({ params }) => {
        const ticket = mockTickets.find(t => t.id === Number(params.id));
        return ticket ? HttpResponse.json(ticket) : HttpResponse.json({ message: 'Not found' }, { status: 404 });
    }),

    http.post('/api/tickets', async ({ request }) => {
        const body = await request.json() as Record<string, unknown>;
        return HttpResponse.json({
            id: 100,
            passengerName: body.passengerName,
            passengerEmail: body.passengerEmail,
            categoryName: 'Monthly Pass',
            busLineName: body.busLineId ? 'Line 1 - Downtown Loop' : null,
            purchaseDate: '2026-02-07T12:00:00',
            validFrom: (body.validFrom as string) || '2026-02-07T12:00:00',
            expirationDate: '2026-03-09T12:00:00',
            active: true,
            expired: false,
            daysRemaining: 30,
        }, { status: 201 });
    }),

    http.post('/api/tickets/:id/renew', ({ params }) => {
        const ticket = mockTickets.find(t => t.id === Number(params.id));
        if (!ticket) return HttpResponse.json({ message: 'Not found' }, { status: 404 });
        if (ticket.categoryName === 'Single Ride') {
            return HttpResponse.json({ message: 'Single ride tickets cannot be renewed' }, { status: 400 });
        }
        return HttpResponse.json({ ...ticket, daysRemaining: 30, active: true, expired: false });
    }),

    http.delete('/api/tickets/:id', () => new HttpResponse(null, { status: 204 })),
];