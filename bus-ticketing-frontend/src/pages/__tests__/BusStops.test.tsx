import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect } from 'vitest';
import BusStops from '../BusStops';

const renderWithRouter = (ui: React.ReactElement) =>
    render(<BrowserRouter>{ui}</BrowserRouter>);

describe('BusStops Page', () => {
    it('renders the page title', () => {
        renderWithRouter(<BusStops />);
        expect(screen.getByText('Bus Stops')).toBeInTheDocument();
    });

    it('loads and displays bus stops from API', async () => {
        renderWithRouter(<BusStops />);

        await waitFor(() => {
            expect(screen.getByText('Central Station')).toBeInTheDocument();
            expect(screen.getByText('City Hall')).toBeInTheDocument();
            expect(screen.getByText('Old Market Square')).toBeInTheDocument();
        });
    });

    it('displays coordinates for each stop', async () => {
        renderWithRouter(<BusStops />);

        await waitFor(() => {
            expect(screen.getByText('41.9973')).toBeInTheDocument();
            expect(screen.getByText('21.428')).toBeInTheDocument();
        });
    });

    it('opens create modal', async () => {
        const user = userEvent.setup();
        renderWithRouter(<BusStops />);

        await user.click(screen.getByText('+ New Stop'));

        expect(screen.getByText('New Stop')).toBeInTheDocument();
        const modal = screen.getByText('New Stop').closest('.modal')!;
        expect(modal.querySelectorAll('input').length).toBeGreaterThanOrEqual(1);
    });

    it('opens edit modal with pre-filled data', async () => {
        const user = userEvent.setup();
        renderWithRouter(<BusStops />);

        await waitFor(() => {
            expect(screen.getByText('Central Station')).toBeInTheDocument();
        });

        const editButtons = screen.getAllByText('Edit');
        await user.click(editButtons[0]);

        expect(screen.getByText('Edit Stop')).toBeInTheDocument();
        expect(screen.getByDisplayValue('Central Station')).toBeInTheDocument();
        expect(screen.getByDisplayValue('41.9973')).toBeInTheDocument();
    });

    it('submits new stop and closes modal', async () => {
        const user = userEvent.setup();
        renderWithRouter(<BusStops />);

        await user.click(screen.getByText('+ New Stop'));

        const modal = screen.getByText('New Stop').closest('.modal')!;
        const inputs = modal.querySelectorAll('input');

        await user.type(inputs[0], 'Test Stop');
        await user.type(inputs[1], '42.0');
        await user.type(inputs[2], '21.5');
        await user.click(screen.getByText('Save'));

        await waitFor(() => {
            expect(screen.queryByText('New Stop')).not.toBeInTheDocument();
        });
    });

    it('closes modal on Cancel', async () => {
        const user = userEvent.setup();
        renderWithRouter(<BusStops />);

        await user.click(screen.getByText('+ New Stop'));
        await user.click(screen.getByText('Cancel'));

        expect(screen.queryByText('New Stop')).not.toBeInTheDocument();
    });
});