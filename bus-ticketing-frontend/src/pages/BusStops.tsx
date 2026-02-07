import { useEffect, useState } from 'react'
import { busStopApi } from '../api'
import type { BusStop } from '../types'

export default function BusStops() {
    const [stops, setStops] = useState<BusStop[]>([])
    const [showForm, setShowForm] = useState(false)
    const [editing, setEditing] = useState<BusStop | null>(null)
    const [form, setForm] = useState({ name: '', latitude: '', longitude: '' })
    const [error, setError] = useState('')

    const load = () => busStopApi.getAll().then(setStops)

    useEffect(() => { load() }, [])

    const openCreate = () => { setEditing(null); setForm({ name: '', latitude: '', longitude: '' }); setShowForm(true) }
    const openEdit = (s: BusStop) => {
        setEditing(s)
        setForm({ name: s.name, latitude: s.latitude?.toString() || '', longitude: s.longitude?.toString() || '' })
        setShowForm(true)
    }

    const handleSubmit = async () => {
        const data = {
            name: form.name,
            latitude: form.latitude ? parseFloat(form.latitude) : undefined,
            longitude: form.longitude ? parseFloat(form.longitude) : undefined,
        }
        try {
            if (editing) await busStopApi.update(editing.id, data)
            else await busStopApi.create(data)
            setShowForm(false); load()
        } catch (e: unknown) { setError(e instanceof Error ? e.message : 'Error') }
    }

    const handleDelete = async (id: number) => {
        if (!confirm('Delete this stop?')) return
        try { await busStopApi.delete(id); load() }
        catch (e: unknown) { setError(e instanceof Error ? e.message : 'Error') }
    }

    return (
        <>
            <div className="flex-between">
                <h1>Bus Stops</h1>
                <button className="btn btn-primary" onClick={openCreate}>+ New Stop</button>
            </div>
            {error && <div className="alert alert-error">{error}</div>}
            <div className="card">
                <table>
                    <thead><tr><th>ID</th><th>Name</th><th>Latitude</th><th>Longitude</th><th>Actions</th></tr></thead>
                    <tbody>
                    {stops.map(s => (
                        <tr key={s.id}>
                            <td>{s.id}</td>
                            <td>{s.name}</td>
                            <td>{s.latitude ?? '-'}</td>
                            <td>{s.longitude ?? '-'}</td>
                            <td>
                                <div className="btn-group">
                                    <button className="btn btn-secondary btn-sm" onClick={() => openEdit(s)}>Edit</button>
                                    <button className="btn btn-danger btn-sm" onClick={() => handleDelete(s.id)}>Delete</button>
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
                        <h2>{editing ? 'Edit Stop' : 'New Stop'}</h2>
                        <div className="form-group">
                            <label>Name</label>
                            <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} />
                        </div>
                        <div className="form-row">
                            <div className="form-group">
                                <label>Latitude</label>
                                <input type="number" step="any" value={form.latitude} onChange={e => setForm({ ...form, latitude: e.target.value })} />
                            </div>
                            <div className="form-group">
                                <label>Longitude</label>
                                <input type="number" step="any" value={form.longitude} onChange={e => setForm({ ...form, longitude: e.target.value })} />
                            </div>
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