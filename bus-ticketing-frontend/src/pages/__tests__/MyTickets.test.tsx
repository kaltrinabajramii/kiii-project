import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect } from 'vitest';
import MyTickets from '../MyTickets';

const renderWithRouter = (ui: React.ReactElement) =>
    render(<BrowserRouter>{ui}</BrowserRouter>);

describe('MyTickets Page', () => {
    it('renders the page title and search form', () => {
        renderWithRouter(<MyTickets />);
        expect(screen.getByText('My Tickets')).toBeInTheDocument();
        expect(screen.getByPlaceholderText('john@example.com')).toBeInTheDocument();
        expect(screen.getByText('Search')).toBeInTheDocument();
    });

    it('does not show results before search', () => {
        renderWithRouter(<MyTickets />);
        expect(screen.queryByText('John Doe')).not.toBeInTheDocument();
    });

    it('shows all tickets after searching with empty email', async () => {
        const user = userEvent.setup();
        renderWithRouter(<MyTickets />);

        await user.click(screen.getByText('Search'));

        await waitFor(() => {
            // John Doe appears twice (2 tickets), so use getAllByText
            expect(screen.getAllByText('John Doe').length).toBeGreaterThanOrEqual(1);
            expect(screen.getByText('Jane Smith')).toBeInTheDocument();
        });
    });

    it('filters tickets by email', async () => {
        const user = userEvent.setup();
        renderWithRouter(<MyTickets />);

        await user.type(screen.getByPlaceholderText('john@example.com'), 'john@example.com');
        await user.click(screen.getByText('Search'));

        await waitFor(() => {
            // John has 2 tickets in mock data
            expect(screen.getAllByText('John Doe').length).toBe(2);
            expect(screen.queryByText('Jane Smith')).not.toBeInTheDocument();
        });
    });

    it('shows correct status badges', async () => {
        const user = userEvent.setup();
        renderWithRouter(<MyTickets />);

        await user.click(screen.getByText('Search'));

        await waitFor(() => {
            expect(screen.getByText(/Active \(25d left\)/)).toBeInTheDocument();
            // Jane's ticket is inactive (active=false), so it shows "Cancelled" not "Expired"
            expect(screen.getByText('Cancelled')).toBeInTheDocument();
            expect(screen.getByText(/Expiring Soon \(5d\)/)).toBeInTheDocument();
        });
    });

    it('shows cancelled badge for inactive tickets', async () => {
        const user = userEvent.setup();
        renderWithRouter(<MyTickets />);

        await user.click(screen.getByText('Search'));

        await waitFor(() => {
            expect(screen.getByText('Cancelled')).toBeInTheDocument();
        });
    });

    it('shows "All lines" for tickets without a bus line', async () => {
        const user = userEvent.setup();
        renderWithRouter(<MyTickets />);

        await user.click(screen.getByText('Search'));

        await waitFor(() => {
            const allLinesTexts = screen.getAllByText('All lines');
            expect(allLinesTexts.length).toBeGreaterThanOrEqual(1);
        });
    });

    it('shows Renew button for active renewable tickets', async () => {
        const user = userEvent.setup();
        renderWithRouter(<MyTickets />);

        await user.click(screen.getByText('Search'));

        await waitFor(() => {
            expect(screen.getAllByText('Renew').length).toBeGreaterThanOrEqual(1);
        });
    });

    it('shows Cancel button for active tickets', async () => {
        const user = userEvent.setup();
        renderWithRouter(<MyTickets />);

        await user.click(screen.getByText('Search'));

        await waitFor(() => {
            expect(screen.getAllByText('Cancel').length).toBeGreaterThanOrEqual(1);
        });
    });

    it('shows "No tickets found" when search returns empty', async () => {
        const user = userEvent.setup();
        renderWithRouter(<MyTickets />);

        await user.type(screen.getByPlaceholderText('john@example.com'), 'nobody@example.com');
        await user.click(screen.getByText('Search'));

        await waitFor(() => {
            expect(screen.getByText('No tickets found.')).toBeInTheDocument();
        });
    });

    it('has a status filter dropdown', () => {
        renderWithRouter(<MyTickets />);
        expect(screen.getByText('All')).toBeInTheDocument();
        expect(screen.getByText('Active only')).toBeInTheDocument();
        expect(screen.getByText('Inactive only')).toBeInTheDocument();
    });
});