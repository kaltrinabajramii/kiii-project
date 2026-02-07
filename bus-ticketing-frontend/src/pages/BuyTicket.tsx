import { useEffect, useState } from 'react'
import { busLineApi, categoryApi, ticketApi } from '../api'
import type { BusLine, TicketCategory, Ticket } from '../types'

export default function BuyTicket() {
    const [categories, setCategories] = useState<TicketCategory[]>([])
    const [lines, setLines] = useState<BusLine[]>([])
    const [form, setForm] = useState({ passengerName: '', passengerEmail: '', ticketCategoryId: '', busLineId: '', validFrom: '' })
    const [result, setResult] = useState<Ticket | null>(null)
    const [error, setError] = useState('')

    useEffect(() => {
        categoryApi.getAll().then(setCategories)
        busLineApi.getAll().then(setLines)
    }, [])

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setError(''); setResult(null)
        try {
            const ticket = await ticketApi.purchase({
                passengerName: form.passengerName,
                passengerEmail: form.passengerEmail,
                ticketCategoryId: parseInt(form.ticketCategoryId),
                busLineId: form.busLineId ? parseInt(form.busLineId) : null,
                validFrom: form.validFrom || undefined,
            })
            setResult(ticket)
        } catch (err: unknown) { setError(err instanceof Error ? err.message : 'Purchase failed') }
    }

    const selectedCat = categories.find(c => c.id === parseInt(form.ticketCategoryId))

    if (result) {
        return (
            <>
                <h1>Buy Ticket</h1>
                <div className="card">
                    <div className="alert alert-success">Ticket purchased successfully!</div>
                    <table>
                        <tbody>
                        <tr><td><strong>Ticket ID</strong></td><td>#{result.id}</td></tr>
                        <tr><td><strong>Passenger</strong></td><td>{result.passengerName}</td></tr>
                        <tr><td><strong>Email</strong></td><td>{result.passengerEmail}</td></tr>
                        <tr><td><strong>Category</strong></td><td>{result.categoryName}</td></tr>
                        <tr><td><strong>Bus Line</strong></td><td>{result.busLineName || 'All lines'}</td></tr>
                        <tr><td><strong>Valid From</strong></td><td>{new Date(result.validFrom).toLocaleString()}</td></tr>
                        <tr><td><strong>Expires</strong></td><td>{new Date(result.expirationDate).toLocaleString()}</td></tr>
                        </tbody>
                    </table>
                    <button className="btn btn-primary mt-16" onClick={() => {
                        setResult(null)
                        setForm({ passengerName: '', passengerEmail: '', ticketCategoryId: '', busLineId: '', validFrom: '' })
                    }}>
                        Buy Another
                    </button>
                </div>
            </>
        )
    }

    return (
        <>
            <h1>Buy Ticket</h1>
            <div className="card">
                {error && <div className="alert alert-error">{error}</div>}
                <form onSubmit={handleSubmit}>
                    <div className="form-row">
                        <div className="form-group">
                            <label>Passenger Name *</label>
                            <input required value={form.passengerName} onChange={e => setForm({ ...form, passengerName: e.target.value })} />
                        </div>
                        <div className="form-group">
                            <label>Email *</label>
                            <input type="email" required value={form.passengerEmail} onChange={e => setForm({ ...form, passengerEmail: e.target.value })} />
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Ticket Category *</label>
                        <select required value={form.ticketCategoryId} onChange={e => setForm({ ...form, ticketCategoryId: e.target.value })}>
                            <option value="">-- Select category --</option>
                            {categories.map(c => (
                                <option key={c.id} value={c.id}>
                                    {c.name} — €{Number(c.price).toFixed(2)} ({c.durationDays === 0 ? 'single ride' : `${c.durationDays} days`})
                                </option>
                            ))}
                        </select>
                    </div>
                    {selectedCat && <p className="text-muted mb-16">{selectedCat.description}</p>}

                    <div className="form-row">
                        <div className="form-group">
                            <label>Bus Line (optional, leave blank for all lines)</label>
                            <select value={form.busLineId} onChange={e => setForm({ ...form, busLineId: e.target.value })}>
                                <option value="">All lines</option>
                                {lines.filter(l => l.active).map(l => (
                                    <option key={l.id} value={l.id}>{l.name}</option>
                                ))}
                            </select>
                        </div>
                        <div className="form-group">
                            <label>Valid From (optional, defaults to now)</label>
                            <input type="datetime-local" value={form.validFrom} onChange={e => setForm({ ...form, validFrom: e.target.value })} />
                        </div>
                    </div>

                    {selectedCat && (
                        <div className="card" style={{ background: '#f0f9ff', border: '1px solid #bae6fd' }}>
                            <strong>Summary:</strong> {selectedCat.name} — €{Number(selectedCat.price).toFixed(2)}
                            {form.busLineId ? ` on ${lines.find(l => l.id === parseInt(form.busLineId))?.name}` : ' on all lines'}
                        </div>
                    )}

                    <button type="submit" className="btn btn-success mt-16">Purchase Ticket</button>
                </form>
            </div>
        </>
    )
}