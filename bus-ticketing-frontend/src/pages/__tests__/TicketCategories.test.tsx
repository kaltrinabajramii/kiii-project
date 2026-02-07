import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect } from 'vitest';
import TicketCategories from '../TicketCategories';

const renderWithRouter = (ui: React.ReactElement) =>
    render(<BrowserRouter>{ui}</BrowserRouter>);

describe('TicketCategories Page', () => {
    it('renders the page title', () => {
        renderWithRouter(<TicketCategories />);
        expect(screen.getByText('Ticket Categories')).toBeInTheDocument();
    });

    it('loads and displays categories from API', async () => {
        renderWithRouter(<TicketCategories />);

        await waitFor(() => {
            expect(screen.getByText('Single Ride')).toBeInTheDocument();
            expect(screen.getByText('Monthly Pass')).toBeInTheDocument();
            expect(screen.getByText('Weekly Pass')).toBeInTheDocument();
        });
    });

    it('displays duration correctly for single ride', async () => {
        renderWithRouter(<TicketCategories />);

        await waitFor(() => {
            expect(screen.getByText('Single ride')).toBeInTheDocument();
        });
    });

    it('displays duration correctly for multi-day passes', async () => {
        renderWithRouter(<TicketCategories />);

        await waitFor(() => {
            expect(screen.getByText('30 days')).toBeInTheDocument();
            expect(screen.getByText('7 days')).toBeInTheDocument();
        });
    });

    it('displays formatted prices', async () => {
        renderWithRouter(<TicketCategories />);

        await waitFor(() => {
            expect(screen.getByText('€1.50')).toBeInTheDocument();
            expect(screen.getByText('€45.00')).toBeInTheDocument();
        });
    });

    it('opens create modal', async () => {
        const user = userEvent.setup();
        renderWithRouter(<TicketCategories />);

        await user.click(screen.getByText('+ New Category'));

        expect(screen.getByText('New Category')).toBeInTheDocument();
    });

    it('opens edit modal with pre-filled values', async () => {
        const user = userEvent.setup();
        renderWithRouter(<TicketCategories />);

        await waitFor(() => {
            expect(screen.getByText('Monthly Pass')).toBeInTheDocument();
        });

        const editButtons = screen.getAllByText('Edit');
        await user.click(editButtons[1]); // Monthly Pass

        expect(screen.getByText('Edit Category')).toBeInTheDocument();
        expect(screen.getByDisplayValue('Monthly Pass')).toBeInTheDocument();
        expect(screen.getByDisplayValue('30')).toBeInTheDocument();
        expect(screen.getByDisplayValue('45')).toBeInTheDocument();
    });

    it('submits new category and closes modal', async () => {
        const user = userEvent.setup();
        renderWithRouter(<TicketCategories />);

        await user.click(screen.getByText('+ New Category'));

        const modal = screen.getByText('New Category').closest('.modal')!;
        const inputs = modal.querySelectorAll('input');

        // inputs[0] = Name, inputs[1] = Duration, inputs[2] = Price
        await user.type(inputs[0], 'Quarterly Pass');

        await user.click(screen.getByText('Save'));

        await waitFor(() => {
            expect(screen.queryByText('New Category')).not.toBeInTheDocument();
        });
    });
});