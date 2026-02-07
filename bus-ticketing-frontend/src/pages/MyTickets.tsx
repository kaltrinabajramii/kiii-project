import { useState } from 'react'
import { ticketApi } from '../api'
import type { Ticket } from '../types'

function statusBadge(t: Ticket) {
    if (!t.active) return <span className="badge badge-gray">Cancelled</span>
    if (t.expired) return <span className="badge badge-red">Expired</span>
    if (t.daysRemaining <= 7) return <span className="badge badge-yellow">Expiring Soon ({t.daysRemaining}d)</span>
    return <span className="badge badge-green">Active ({t.daysRemaining}d left)</span>
}

export default function MyTickets() {
    const [email, setEmail] = useState('')
    const [tickets, setTickets] = useState<Ticket[]>([])
    const [total, setTotal] = useState(0)
    const [page, setPage] = useState(0)
    const [searched, setSearched] = useState(false)
    const [error, setError] = useState('')
    const [filterActive, setFilterActive] = useState<string>('')
    const pageSize = 10

    const load = async (p = 0, activeFilter = filterActive) => {
        setError('')
        try {
            const params: { email?: string; active?: boolean; page: number; size: number } = { page: p, size: pageSize }
            if (email.trim()) params.email = email.trim()
            if (activeFilter === 'true') params.active = true
            if (activeFilter === 'false') params.active = false
            const res = await ticketApi.getAll(params)
            setTickets(res.content)
            setTotal(res.totalPages)
            setPage(p)
            setSearched(true)
        } catch (err: unknown) { setError(err instanceof Error ? err.message : 'Failed to load') }
    }

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault()
        load(0)
    }

    const handleRenew = async (id: number) => {
        try {
            await ticketApi.renew(id)
            load(page)
        } catch (err: unknown) { setError(err instanceof Error ? err.message : 'Renew failed') }
    }

    const handleCancel = async (id: number) => {
        if (!confirm('Cancel this ticket?')) return
        try {
            await ticketApi.cancel(id)
            load(page)
        } catch (err: unknown) { setError(err instanceof Error ? err.message : 'Cancel failed') }
    }

    return (
        <>
            <h1>My Tickets</h1>

            <div className="card">
                <form onSubmit={handleSearch}>
                    <div className="form-row">
                        <div className="form-group">
                            <label>Email (leave blank for all tickets)</label>
                            <input type="email" placeholder="john@example.com" value={email} onChange={e => setEmail(e.target.value)} />
                        </div>
                        <div className="form-group">
                            <label>Status Filter</label>
                            <select value={filterActive} onChange={e => { setFilterActive(e.target.value); if (searched) load(0, e.target.value) }}>
                                <option value="">All</option>
                                <option value="true">Active only</option>
                                <option value="false">Inactive only</option>
                            </select>
                        </div>
                    </div>
                    <button type="submit" className="btn btn-primary">Search</button>
                </form>
            </div>

            {error && <div className="alert alert-error">{error}</div>}

            {searched && (
                <div className="card">
                    {tickets.length === 0 ? (
                        <p className="text-muted">No tickets found.</p>
                    ) : (
                        <>
                            <table>
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Passenger</th>
                                    <th>Category</th>
                                    <th>Bus Line</th>
                                    <th>Valid From</th>
                                    <th>Expires</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                                </thead>
                                <tbody>
                                {tickets.map(t => (
                                    <tr key={t.id}>
                                        <td>#{t.id}</td>
                                        <td>
                                            <div>{t.passengerName}</div>
                                            <div className="text-muted">{t.passengerEmail}</div>
                                        </td>
                                        <td>{t.categoryName}</td>
                                        <td>{t.busLineName || 'All lines'}</td>
                                        <td>{new Date(t.validFrom).toLocaleDateString()}</td>
                                        <td>{new Date(t.expirationDate).toLocaleDateString()}</td>
                                        <td>{statusBadge(t)}</td>
                                        <td>
                                            <div className="btn-group">
                                                {t.active && !t.expired && t.categoryName !== 'Single Ride' && (
                                                    <button className="btn btn-success btn-sm" onClick={() => handleRenew(t.id)}>Renew</button>
                                                )}
                                                {t.active && !t.expired && (
                                                    <button className="btn btn-danger btn-sm" onClick={() => handleCancel(t.id)}>Cancel</button>
                                                )}
                                                {!t.active && t.categoryName !== 'Single Ride' && (
                                                    <button className="btn btn-success btn-sm" onClick={() => handleRenew(t.id)}>Reactivate</button>
                                                )}
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>

                            {total > 1 && (
                                <div className="pagination">
                                    <button className="btn btn-secondary btn-sm" disabled={page === 0} onClick={() => load(page - 1)}>← Prev</button>
                                    <span className="text-muted">Page {page + 1} of {total}</span>
                                    <button className="btn btn-secondary btn-sm" disabled={page >= total - 1} onClick={() => load(page + 1)}>Next →</button>
                                </div>
                            )}
                        </>
                    )}
                </div>
            )}
        </>
    )
}