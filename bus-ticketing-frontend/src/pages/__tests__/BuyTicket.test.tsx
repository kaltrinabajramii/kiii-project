import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect } from 'vitest';
import BuyTicket from '../BuyTicket';

const renderWithRouter = (ui: React.ReactElement) =>
    render(<BrowserRouter>{ui}</BrowserRouter>);

// Helper: get form elements by their position since labels lack htmlFor
function getFormElements() {
    const inputs = document.querySelectorAll<HTMLInputElement>('form input');
    const selects = document.querySelectorAll<HTMLSelectElement>('form select');
    return {
        nameInput: inputs[0],        // Passenger Name
        emailInput: inputs[1],       // Email
        categorySelect: selects[0],  // Ticket Category
        busLineSelect: selects[1],   // Bus Line
    };
}

describe('BuyTicket Page', () => {
    it('renders the page title and form', () => {
        renderWithRouter(<BuyTicket />);
        expect(screen.getByText('Buy Ticket')).toBeInTheDocument();
        expect(screen.getByText('Passenger Name *')).toBeInTheDocument();
        expect(screen.getByText('Email *')).toBeInTheDocument();
        expect(screen.getByText('Ticket Category *')).toBeInTheDocument();
    });

    it('loads categories into the dropdown', async () => {
        renderWithRouter(<BuyTicket />);

        await waitFor(() => {
            expect(screen.getByText(/Single Ride/)).toBeInTheDocument();
            expect(screen.getByText(/Monthly Pass/)).toBeInTheDocument();
        });
    });

    it('loads active bus lines into the dropdown', async () => {
        renderWithRouter(<BuyTicket />);

        await waitFor(() => {
            expect(screen.getByText('Line 1 - Downtown Loop')).toBeInTheDocument();
            expect(screen.getByText('Line 2 - Airport Express')).toBeInTheDocument();
        });
    });

    it('shows category description when category is selected', async () => {
        const user = userEvent.setup();
        renderWithRouter(<BuyTicket />);

        await waitFor(() => {
            expect(screen.getByText(/Monthly Pass/)).toBeInTheDocument();
        });

        const { categorySelect } = getFormElements();
        await user.selectOptions(categorySelect, '2');

        await waitFor(() => {
            expect(screen.getByText('Valid for 30 days')).toBeInTheDocument();
        });
    });

    it('shows price summary when category is selected', async () => {
        const user = userEvent.setup();
        renderWithRouter(<BuyTicket />);

        await waitFor(() => {
            expect(screen.getByText(/Monthly Pass/)).toBeInTheDocument();
        });

        const { categorySelect } = getFormElements();
        await user.selectOptions(categorySelect, '2');

        await waitFor(() => {
            // Price appears in both the option and the summary card, so use getAllByText
            expect(screen.getAllByText(/€45\.00/).length).toBeGreaterThanOrEqual(2);
            expect(screen.getByText(/Summary:/)).toBeInTheDocument();
        });
    });

    it('submits form and shows success confirmation', async () => {
        const user = userEvent.setup();
        renderWithRouter(<BuyTicket />);

        await waitFor(() => {
            expect(screen.getByText(/Monthly Pass/)).toBeInTheDocument();
        });

        const { nameInput, emailInput, categorySelect } = getFormElements();
        await user.type(nameInput, 'Test User');
        await user.type(emailInput, 'test@example.com');
        await user.selectOptions(categorySelect, '2');

        await user.click(screen.getByText('Purchase Ticket'));

        await waitFor(() => {
            expect(screen.getByText('Ticket purchased successfully!')).toBeInTheDocument();
            expect(screen.getByText('#100')).toBeInTheDocument();
            expect(screen.getByText('Test User')).toBeInTheDocument();
        });
    });

    it('shows "Buy Another" button after purchase', async () => {
        const user = userEvent.setup();
        renderWithRouter(<BuyTicket />);

        await waitFor(() => {
            expect(screen.getByText(/Monthly Pass/)).toBeInTheDocument();
        });

        const { nameInput, emailInput, categorySelect } = getFormElements();
        await user.type(nameInput, 'Test User');
        await user.type(emailInput, 'test@example.com');
        await user.selectOptions(categorySelect, '2');
        await user.click(screen.getByText('Purchase Ticket'));

        await waitFor(() => {
            expect(screen.getByText('Buy Another')).toBeInTheDocument();
        });
    });

    it('resets form when clicking "Buy Another"', async () => {
        const user = userEvent.setup();
        renderWithRouter(<BuyTicket />);

        await waitFor(() => {
            expect(screen.getByText(/Monthly Pass/)).toBeInTheDocument();
        });

        const { nameInput, emailInput, categorySelect } = getFormElements();
        await user.type(nameInput, 'Test User');
        await user.type(emailInput, 'test@example.com');
        await user.selectOptions(categorySelect, '2');
        await user.click(screen.getByText('Purchase Ticket'));

        await waitFor(() => {
            expect(screen.getByText('Buy Another')).toBeInTheDocument();
        });

        await user.click(screen.getByText('Buy Another'));

        // After reset, form inputs should be empty
        const resetInputs = document.querySelectorAll<HTMLInputElement>('form input');
        expect(resetInputs[0].value).toBe('');
        expect(resetInputs[1].value).toBe('');
    });
});