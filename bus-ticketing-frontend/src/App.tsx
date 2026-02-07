import { Routes, Route, NavLink } from 'react-router-dom'
import Home from './pages/Home'
import BusLines from './pages/BusLines'
import BusLineDetail from './pages/BusLineDetail'
import BusStops from './pages/BusStops'
import TicketCategories from './pages/TicketCategories'
import BuyTicket from './pages/BuyTicket'
import MyTickets from './pages/MyTickets.tsx'

export default function App() {
    return (
        <>
            <nav>
                <NavLink to="/" className="brand">🚌 BusTickets</NavLink>
                <NavLink to="/">Home</NavLink>
                <NavLink to="/bus-lines">Bus Lines</NavLink>
                <NavLink to="/bus-stops">Bus Stops</NavLink>
                <NavLink to="/ticket-categories">Categories</NavLink>
                <NavLink to="/buy-ticket">Buy Ticket</NavLink>
                <NavLink to="/my-tickets">My Tickets</NavLink>
            </nav>
            <div className="app">
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/bus-lines" element={<BusLines />} />
                    <Route path="/bus-lines/:id" element={<BusLineDetail />} />
                    <Route path="/bus-stops" element={<BusStops />} />
                    <Route path="/ticket-categories" element={<TicketCategories />} />
                    <Route path="/buy-ticket" element={<BuyTicket />} />
                    <Route path="/my-tickets" element={<MyTickets />} />
                </Routes>
            </div>
        </>
    )
}