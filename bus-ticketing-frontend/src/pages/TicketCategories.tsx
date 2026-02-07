import { useEffect, useState } from 'react'
import { categoryApi } from '../api'
import type { TicketCategory } from '../types'

export default function TicketCategories() {
    const [cats, setCats] = useState<TicketCategory[]>([])
    const [showForm, setShowForm] = useState(false)
    const [editing, setEditing] = useState<TicketCategory | null>(null)
    const [form, setForm] = useState({ name: '', durationDays: '0', price: '0', description: '' })
    const [error, setError] = useState('')

    const load = () => categoryApi.getAll().then(setCats)
    useEffect(() => { load() }, [])

    const openCreate = () => { setEditing(null); setForm({ name: '', durationDays: '0', price: '0', description: '' }); setShowForm(true) }
    const openEdit = (c: TicketCategory) => {
        setEditing(c)
        setForm({ name: c.name, durationDays: String(c.durationDays), price: String(c.price), description: c.description || '' })
        setShowForm(true)
    }

    const handleSubmit = async () => {
        const data = { name: form.name, durationDays: parseInt(form.durationDays), price: parseFloat(form.price), description: form.description || undefined }
        try {
            if (editing) await categoryApi.update(editing.id, data)
            else await categoryApi.create(data)
            setShowForm(false); load()
        } catch (e: unknown) { setError(e instanceof Error ? e.message : 'Error') }
    }

    const handleDelete = async (id: number) => {
        if (!confirm('Delete this category?')) return
        await categoryApi.delete(id); load()
    }

    return (
        <>
            <div className="flex-between">
                <h1>Ticket Categories</h1>
                <button className="btn btn-primary" onClick={openCreate}>+ New Category</button>
            </div>
            {error && <div className="alert alert-error">{error}</div>}
            <div className="card">
                <table>
                    <thead><tr><th>Name</th><th>Duration</th><th>Price</th><th>Description</th><th>Actions</th></tr></thead>
                    <tbody>
                    {cats.map(c => (
                        <tr key={c.id}>
                            <td>{c.name}</td>
                            <td>{c.durationDays === 0 ? 'Single ride' : `${c.durationDays} days`}</td>
                            <td>€{Number(c.price).toFixed(2)}</td>
                            <td className="text-muted">{c.description}</td>
                            <td>
                                <div className="btn-group">
                                    <button className="btn btn-secondary btn-sm" onClick={() => openEdit(c)}>Edit</button>
                                    <button className="btn btn-danger btn-sm" onClick={() => handleDelete(c.id)}>Delete</button>
                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            {showForm && (
                <div className="modal-overlay" onClick={() => setShowForm(false)}>
                    <div className="modal" onClick={e => e.stopPropagation()}>
                        <h2>{editing ? 'Edit Category' : 'New Category'}</h2>
                        <div className="form-group">
                            <label>Name</label>
                            <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} />
                        </div>
                        <div className="form-row">
                            <div className="form-group">
                                <label>Duration (days, 0 = single ride)</label>
                                <input type="number" min="0" value={form.durationDays} onChange={e => setForm({ ...form, durationDays: e.target.value })} />
                            </div>
                            <div className="form-group">
                                <label>Price (€)</label>
                                <input type="number" min="0" step="0.01" value={form.price} onChange={e => setForm({ ...form, price: e.target.value })} />
                            </div>
                        </div>
                        <div className="form-group">
                            <label>Description</label>
                            <textarea value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} rows={2} />
                        </div>
                        <div className="btn-group mt-16">
                            <button className="btn btn-primary" onClick={handleSubmit}>Save</button>
                            <button className="btn btn-secondary" onClick={() => setShowForm(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}
        </>
    )
}