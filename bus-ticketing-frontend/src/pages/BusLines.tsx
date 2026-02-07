import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { busLineApi, busStopApi } from '../api'
import type { BusLine, BusStop } from '../types'

export default function BusLines() {
    const [lines, setLines] = useState<BusLine[]>([])
    const [allStops, setAllStops] = useState<BusStop[]>([])
    const [showForm, setShowForm] = useState(false)
    const [editing, setEditing] = useState<BusLine | null>(null)
    const [form, setForm] = useState({ name: '', description: '' })
    const [routeStopIds, setRouteStopIds] = useState<number[]>([])
    const [editingRoute, setEditingRoute] = useState(false)
    const [error, setError] = useState('')

    const load = () => {
        busLineApi.getAll().then(setLines).catch(() => setError('Failed to load'))
        busStopApi.getAll().then(setAllStops)
    }

    useEffect(() => { load() }, [])

    const openCreate = () => {
        setEditing(null)
        setForm({ name: '', description: '' })
        setRouteStopIds([])
        setEditingRoute(true)
        setShowForm(true)
    }

    const openEdit = (l: BusLine) => {
        setEditing(l)
        setForm({ name: l.name, description: l.description || '' })
        setRouteStopIds(l.stops.map(s => s.stopId))
        setEditingRoute(false)
        setShowForm(true)
    }

    const openRouteEdit = (l: BusLine) => {
        setEditing(l)
        setForm({ name: l.name, description: l.description || '' })
        setRouteStopIds(l.stops.map(s => s.stopId))
        setEditingRoute(true)
        setShowForm(true)
    }

    const addRouteStop = (stopId: number) => setRouteStopIds([...routeStopIds, stopId])
    const removeRouteStop = (idx: number) => setRouteStopIds(routeStopIds.filter((_, i) => i !== idx))
    const moveRouteStop = (idx: number, direction: 'up' | 'down') => {
        const newStops = [...routeStopIds]
        const targetIdx = direction === 'up' ? idx - 1 : idx + 1
        if (targetIdx < 0 || targetIdx >= newStops.length) return
            ;[newStops[idx], newStops[targetIdx]] = [newStops[targetIdx], newStops[idx]]
        setRouteStopIds(newStops)
    }

    const handleSubmit = async () => {
        try {
            let savedLine: BusLine
            if (editing) {
                savedLine = await busLineApi.update(editing.id, form)
            } else {
                savedLine = await busLineApi.create(form)
            }
            // Save route if route editing tab was used
            if (editingRoute && routeStopIds.length > 0) {
                await busLineApi.setRoute(savedLine.id, routeStopIds)
            }
            setShowForm(false)
            load()
        } catch (e: unknown) { setError(e instanceof Error ? e.message : 'Error') }
    }

    const handleDelete = async (id: number) => {
        if (!confirm('Delete this bus line?')) return
        await busLineApi.delete(id); load()
    }

    return (
        <>
            <div className="flex-between">
                <h1>Bus Lines</h1>
                <button className="btn btn-primary" onClick={openCreate}>+ New Line</button>
            </div>
            {error && <div className="alert alert-error">{error}</div>}
            <div className="card">
                <table>
                    <thead><tr><th>ID</th><th>Name</th><th>Stops</th><th>Status</th><th>Actions</th></tr></thead>
                    <tbody>
                    {lines.map(l => (
                        <tr key={l.id}>
                            <td>{l.id}</td>
                            <td><Link to={`/bus-lines/${l.id}`}>{l.name}</Link></td>
                            <td>{l.stops.length} stops</td>
                            <td><span className={`badge ${l.active ? 'badge-green' : 'badge-red'}`}>{l.active ? 'Active' : 'Inactive'}</span></td>
                            <td>
                                <div className="btn-group">
                                    <button className="btn btn-secondary btn-sm" onClick={() => openEdit(l)}>Edit</button>
                                    <button className="btn btn-primary btn-sm" onClick={() => openRouteEdit(l)}>Manage Route</button>
                                    <button className="btn btn-danger btn-sm" onClick={() => handleDelete(l.id)}>Delete</button>
                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            {showForm && (
                <div className="modal-overlay" onClick={() => setShowForm(false)}>
                    <div className="modal" onClick={e => e.stopPropagation()} style={{ width: 560 }}>
                        <h2>{editing ? (editingRoute ? `Manage Route: ${editing.name}` : 'Edit Bus Line') : 'New Bus Line'}</h2>

                        {/* Tab switcher for editing existing lines */}
                        {editing && (
                            <div className="btn-group mb-16">
                                <button
                                    className={`btn btn-sm ${!editingRoute ? 'btn-primary' : 'btn-secondary'}`}
                                    onClick={() => setEditingRoute(false)}
                                >Details</button>
                                <button
                                    className={`btn btn-sm ${editingRoute ? 'btn-primary' : 'btn-secondary'}`}
                                    onClick={() => setEditingRoute(true)}
                                >Route</button>
                            </div>
                        )}

                        {/* Details tab */}
                        {!editingRoute && (
                            <>
                                <div className="form-group">
                                    <label>Name</label>
                                    <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} />
                                </div>
                                <div className="form-group">
                                    <label>Description</label>
                                    <textarea value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} rows={3} />
                                </div>
                            </>
                        )}

                        {/* Route tab */}
                        {editingRoute && (
                            <>
                                {!editing && (
                                    <>
                                        <div className="form-group">
                                            <label>Name</label>
                                            <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} />
                                        </div>
                                        <div className="form-group">
                                            <label>Description</label>
                                            <textarea value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} rows={2} />
                                        </div>
                                    </>
                                )}
                                <p className="text-muted mb-16" style={{ fontSize: 13 }}>
                                    Add stops in order. Use arrows to reorder.
                                </p>
                                <ul className="route-list" style={{ maxHeight: 250, overflowY: 'auto' }}>
                                    {routeStopIds.map((sId, i) => {
                                        const stop = allStops.find(s => s.id === sId)
                                        return (
                                            <li key={i} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '6px 10px' }}>
                                                <span>
                                                    <span className="order">{i + 1}.</span> {stop?.name || `Stop #${sId}`}
                                                </span>
                                                <div className="btn-group">
                                                    <button className="btn btn-secondary btn-sm" disabled={i === 0} onClick={() => moveRouteStop(i, 'up')}>↑</button>
                                                    <button className="btn btn-secondary btn-sm" disabled={i === routeStopIds.length - 1} onClick={() => moveRouteStop(i, 'down')}>↓</button>
                                                    <button className="btn btn-danger btn-sm" onClick={() => removeRouteStop(i)}>✕</button>
                                                </div>
                                            </li>
                                        )
                                    })}
                                    {routeStopIds.length === 0 && <li className="text-muted">No stops added yet</li>}
                                </ul>
                                <div className="form-group mt-16">
                                    <label>Add Stop</label>
                                    <select onChange={e => { if (e.target.value) addRouteStop(Number(e.target.value)); e.target.value = '' }} defaultValue="">
                                        <option value="">-- Select a stop --</option>
                                        {allStops.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
                                    </select>
                                </div>
                            </>
                        )}

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