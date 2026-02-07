import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { busLineApi, busStopApi } from '../api'
import type { BusLine, BusStop } from '../types'

export default function BusLineDetail() {
    const { id } = useParams<{ id: string }>()
    const [line, setLine] = useState<BusLine | null>(null)
    const [allStops, setAllStops] = useState<BusStop[]>([])
    const [selectedStops, setSelectedStops] = useState<number[]>([])
    const [editingRoute, setEditingRoute] = useState(false)
    const [error, setError] = useState('')

    const load = () => {
        busLineApi.getById(Number(id)).then(setLine).catch(() => setError('Line not found'))
        busStopApi.getAll().then(setAllStops)
    }

    useEffect(() => { load() }, [id])

    const startEditRoute = () => {
        setSelectedStops(line?.stops.map(s => s.stopId) || [])
        setEditingRoute(true)
    }

    const addStop = (stopId: number) => setSelectedStops([...selectedStops, stopId])
    const removeStop = (idx: number) => setSelectedStops(selectedStops.filter((_, i) => i !== idx))

    const moveStop = (idx: number, direction: 'up' | 'down') => {
        const newStops = [...selectedStops]
        const targetIdx = direction === 'up' ? idx - 1 : idx + 1
        if (targetIdx < 0 || targetIdx >= newStops.length) return
            ;[newStops[idx], newStops[targetIdx]] = [newStops[targetIdx], newStops[idx]]
        setSelectedStops(newStops)
    }

    const saveRoute = async () => {
        try {
            await busLineApi.setRoute(Number(id), selectedStops)
            setEditingRoute(false)
            load()
        } catch (e: unknown) { setError(e instanceof Error ? e.message : 'Error') }
    }

    if (!line) return <p>Loading...</p>

    return (
        <>
            <Link to="/bus-lines">← Back to Bus Lines</Link>
            <h1>{line.name}</h1>
            <p className="text-muted">{line.description}</p>
            <span className={`badge ${line.active ? 'badge-green' : 'badge-red'}`}>{line.active ? 'Active' : 'Inactive'}</span>

            {error && <div className="alert alert-error mt-16">{error}</div>}

            <div className="flex-between mt-16">
                <h2>Route ({line.stops.length} stops)</h2>
                {!editingRoute && <button className="btn btn-primary btn-sm" onClick={startEditRoute}>Edit Route</button>}
            </div>

            {!editingRoute ? (
                <ul className="route-list">
                    {line.stops.map(s => (
                        <li key={s.stopOrder}><span className="order">{s.stopOrder}.</span> {s.stopName}</li>
                    ))}
                    {line.stops.length === 0 && <li className="text-muted">No stops assigned</li>}
                </ul>
            ) : (
                <div className="card">
                    <p className="text-muted mb-16">Add stops in order. Use arrows to reorder, ✕ to remove.</p>
                    <ul className="route-list">
                        {selectedStops.map((sId, i) => {
                            const stop = allStops.find(s => s.id === sId)
                            return (
                                <li key={i} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                                    <span>
                                        <span className="order">{i + 1}.</span> {stop?.name || `Stop #${sId}`}
                                    </span>
                                    <div className="btn-group">
                                        <button
                                            className="btn btn-secondary btn-sm"
                                            disabled={i === 0}
                                            onClick={() => moveStop(i, 'up')}
                                            title="Move up"
                                        >↑</button>
                                        <button
                                            className="btn btn-secondary btn-sm"
                                            disabled={i === selectedStops.length - 1}
                                            onClick={() => moveStop(i, 'down')}
                                            title="Move down"
                                        >↓</button>
                                        <button className="btn btn-danger btn-sm" onClick={() => removeStop(i)} title="Remove">✕</button>
                                    </div>
                                </li>
                            )
                        })}
                        {selectedStops.length === 0 && <li className="text-muted">No stops added yet</li>}
                    </ul>
                    <div className="form-group mt-16">
                        <label>Add Stop</label>
                        <select onChange={e => { if (e.target.value) addStop(Number(e.target.value)); e.target.value = '' }} defaultValue="">
                            <option value="">-- Select a stop --</option>
                            {allStops.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
                        </select>
                    </div>
                    <div className="btn-group">
                        <button className="btn btn-success" onClick={saveRoute}>Save Route</button>
                        <button className="btn btn-secondary" onClick={() => setEditingRoute(false)}>Cancel</button>
                    </div>
                </div>
            )}
        </>
    )
}