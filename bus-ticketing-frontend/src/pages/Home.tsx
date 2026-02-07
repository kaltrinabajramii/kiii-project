import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { busLineApi, categoryApi, ticketApi } from '../api'

export default function Home() {
  const [stats, setStats] = useState({ lines: 0, categories: 0, tickets: 0 })

  useEffect(() => {
    Promise.all([busLineApi.getAll(), categoryApi.getAll(), ticketApi.getAll({ size: 1 })]).then(
      ([lines, cats, page]) =>
        setStats({ lines: lines.length, categories: cats.length, tickets: page.totalElements })
    )
  }, [])

  return (
    <>
      <h1>Dashboard</h1>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 16 }}>
        <div className="card">
          <div className="text-muted">Bus Lines</div>
          <div style={{ fontSize: 32, fontWeight: 700 }}>{stats.lines}</div>
          <Link to="/bus-lines" className="btn btn-primary btn-sm mt-8">Manage</Link>
        </div>
        <div className="card">
          <div className="text-muted">Ticket Categories</div>
          <div style={{ fontSize: 32, fontWeight: 700 }}>{stats.categories}</div>
          <Link to="/ticket-categories" className="btn btn-primary btn-sm mt-8">Manage</Link>
        </div>
        <div className="card">
          <div className="text-muted">Total Tickets</div>
          <div style={{ fontSize: 32, fontWeight: 700 }}>{stats.tickets}</div>
          <Link to="/my-tickets" className="btn btn-primary btn-sm mt-8">View</Link>
        </div>
      </div>
    </>
  )
}