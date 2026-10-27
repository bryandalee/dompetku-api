import { NavLink, Outlet } from 'react-router'
import { useAuth } from '../context/AuthContext.jsx'

const NAV_ITEMS = [
  { to: '/', label: 'Ringkasan' },
  { to: '/transactions', label: 'Transaksi' },
  { to: '/categories', label: 'Kategori' },
  { to: '/budgets', label: 'Budget' },
]

function navClass({ isActive }) {
  const base = 'rounded-lg px-3 py-2 text-sm font-semibold whitespace-nowrap transition'
  return isActive ? `${base} bg-brand-soft text-brand` : `${base} text-muted hover:bg-white hover:text-ink`
}

export default function AppLayout() {
  const { user, logout } = useAuth()

  return (
    <div className="min-h-screen lg:flex">
      <aside className="border-b border-line bg-paper lg:sticky lg:top-0 lg:flex lg:h-screen lg:w-60 lg:flex-col lg:border-r lg:border-b-0">
        <div className="flex items-center justify-between px-5 py-4 lg:py-6">
          <span className="text-xl font-extrabold tracking-tight text-brand">DompetKu</span>
          <button type="button" onClick={logout} className="btn-link lg:hidden">
            Keluar
          </button>
        </div>

        <nav className="flex gap-1 overflow-x-auto px-3 pb-3 lg:flex-col lg:px-3">
          {NAV_ITEMS.map((item) => (
            <NavLink key={item.to} to={item.to} end={item.to === '/'} className={navClass}>
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className="mt-auto hidden border-t border-line px-5 py-4 lg:block">
          <p className="truncate text-sm font-semibold">{user.name}</p>
          <p className="truncate text-xs text-muted">{user.email}</p>
          <button type="button" onClick={logout} className="btn-link mt-2">
            Keluar
          </button>
        </div>
      </aside>

      <main className="mx-auto w-full max-w-5xl flex-1 px-4 py-6 sm:px-6 lg:px-10 lg:py-10">
        <Outlet />
      </main>
    </div>
  )
}
