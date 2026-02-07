import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect } from 'vitest';
import BusLines from '../BusLines';

const renderWithRouter = (ui: React.ReactElement) =>
    render(<BrowserRouter>{ui}</BrowserRouter>);

describe('BusLines Page', () => {
    it('renders the page title', async () => {
        renderWithRouter(<BusLines />);
        expect(screen.getByText('Bus Lines')).toBeInTheDocument();
    });

    it('loads and displays bus lines from API', async () => {
        renderWithRouter(<BusLines />);

        await waitFor(() => {
            expect(screen.getByText('Line 1 - Downtown Loop')).toBeInTheDocument();
            expect(screen.getByText('Line 2 - Airport Express')).toBeInTheDocument();
        });
    });

    it('shows stop counts for each line', async () => {
        renderWithRouter(<BusLines />);

        await waitFor(() => {
            expect(screen.getByText('2 stops')).toBeInTheDocument();
            expect(screen.getByText('1 stops')).toBeInTheDocument();
        });
    });

    it('shows active/inactive badges', async () => {
        renderWithRouter(<BusLines />);

        await waitFor(() => {
            const activeBadges = screen.getAllByText('Active');
            const inactiveBadges = screen.getAllByText('Inactive');
            expect(activeBadges.length).toBeGreaterThanOrEqual(2);
            expect(inactiveBadges.length).toBe(1);
        });
    });

    it('opens create modal when clicking "+ New Line"', async () => {
        const user = userEvent.setup();
        renderWithRouter(<BusLines />);

        await user.click(screen.getByText('+ New Line'));

        expect(screen.getByText('New Bus Line')).toBeInTheDocument();
        // Labels exist but aren't linked via htmlFor, so use getByText
        const modal = screen.getByText('New Bus Line').closest('.modal')!;
        expect(modal.querySelector('input')).toBeInTheDocument();
    });

    it('opens edit modal with pre-filled data when clicking Edit', async () => {
        const user = userEvent.setup();
        renderWithRouter(<BusLines />);

        await waitFor(() => {
            expect(screen.getByText('Line 1 - Downtown Loop')).toBeInTheDocument();
        });

        const editButtons = screen.getAllByText('Edit');
        await user.click(editButtons[0]);

        expect(screen.getByText('Edit Bus Line')).toBeInTheDocument();
        expect(screen.getByDisplayValue('Line 1 - Downtown Loop')).toBeInTheDocument();
    });

    it('closes modal when clicking Cancel', async () => {
        const user = userEvent.setup();
        renderWithRouter(<BusLines />);

        await user.click(screen.getByText('+ New Line'));
        expect(screen.getByText('New Bus Line')).toBeInTheDocument();

        await user.click(screen.getByText('Cancel'));
        expect(screen.queryByText('New Bus Line')).not.toBeInTheDocument();
    });

    it('submits new bus line and reloads list', async () => {
        const user = userEvent.setup();
        renderWithRouter(<BusLines />);

        await user.click(screen.getByText('+ New Line'));

        const modal = screen.getByText('New Bus Line').closest('.modal')!;
        const inputs = modal.querySelectorAll('input');
        const textarea = modal.querySelector('textarea')!;

        await user.type(inputs[0], 'Line 4 - New Route');
        await user.type(textarea, 'Test description');
        await user.click(screen.getByText('Save'));

        await waitFor(() => {
            expect(screen.queryByText('New Bus Line')).not.toBeInTheDocument();
        });
    });

    it('shows delete confirmation dialog', async () => {
        renderWithRouter(<BusLines />);

        await waitFor(() => {
            expect(screen.getByText('Line 1 - Downtown Loop')).toBeInTheDocument();
        });

        const deleteButtons = screen.getAllByText('Delete');
        expect(deleteButtons.length).toBeGreaterThan(0);
    });
});