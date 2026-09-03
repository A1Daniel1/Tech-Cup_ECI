import { useState } from 'react'
import { NavLink } from 'react-router'
import { Avatar } from '@/components/atoms/Avatar'
import { Button } from '@/components/atoms/Button'
import { cn } from '@/lib/cn'
import { ROLE_LABELS } from '@/lib/labels'
import type { Role, UserResponse } from '@/types/api'

export interface NavItem {
  to: string
  label: string
  /** Roles allowed to see the link. Empty means any authenticated user. */
  roles?: Role[]
  end?: boolean
}

export interface NavbarProps {
  user: UserResponse | null
  /** Role check resolved by the container (ADMIN implies all). */
  hasRole: (...roles: Role[]) => boolean
  items: NavItem[]
  onLogout: () => void
  loggingOut?: boolean
}

function Brand() {
  return (
    <NavLink to="/" className="flex items-center gap-2 text-gray-900">
      <span className="flex h-8 w-8 items-center justify-center rounded-lg bg-emerald-600 text-white">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" className="h-5 w-5" aria-hidden="true">
          <circle cx="12" cy="12" r="9" />
          <path d="M12 7.5l3.2 2.3-1.2 3.8H10l-1.2-3.8L12 7.5z" />
        </svg>
      </span>
      <span className="text-base font-semibold tracking-tight">
        TechCup <span className="text-emerald-600">Fútbol</span>
      </span>
    </NavLink>
  )
}

function linkClasses(isActive: boolean, mobile = false): string {
  return cn(
    'rounded-lg text-sm font-medium transition-colors',
    mobile ? 'block px-3 py-2' : 'px-3 py-1.5',
    isActive ? 'bg-emerald-50 text-emerald-700' : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900',
  )
}

/** Role-aware top navigation. Presentational: receives the user and the visible items. */
export function Navbar({ user, hasRole, items, onLogout, loggingOut }: NavbarProps) {
  const [open, setOpen] = useState(false)
  const visible = items.filter((item) => !item.roles || item.roles.length === 0 || hasRole(...item.roles))

  return (
    <header className="sticky top-0 z-40 border-b border-gray-200 bg-white/95 backdrop-blur">
      <div className="mx-auto flex h-16 max-w-6xl items-center justify-between gap-4 px-4 sm:px-6">
        <Brand />

        <nav className="hidden items-center gap-1 md:flex" aria-label="Principal">
          {visible.map((item) => (
            <NavLink key={item.to} to={item.to} end={item.end} className={({ isActive }) => linkClasses(isActive)}>
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className="hidden items-center gap-3 md:flex">
          {user && (
            <div className="flex items-center gap-2">
              <Avatar name={user.fullName} size="sm" />
              <div className="leading-tight">
                <p className="text-sm font-medium text-gray-900">{user.fullName}</p>
                <p className="text-xs text-gray-500">{user.roles.map((role) => ROLE_LABELS[role]).join(' · ')}</p>
              </div>
            </div>
          )}
          <Button variant="ghost" size="sm" onClick={onLogout} loading={loggingOut}>
            Cerrar sesión
          </Button>
        </div>

        <button
          type="button"
          className="rounded-lg p-2 text-gray-600 hover:bg-gray-100 md:hidden"
          aria-label={open ? 'Cerrar menú' : 'Abrir menú'}
          aria-expanded={open}
          onClick={() => setOpen((value) => !value)}
        >
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" className="h-6 w-6" aria-hidden="true">
            {open ? <path d="M6 6l12 12M6 18L18 6" /> : <path d="M4 7h16M4 12h16M4 17h16" />}
          </svg>
        </button>
      </div>

      {open && (
        <div className="border-t border-gray-200 bg-white px-4 py-3 md:hidden">
          {user && (
            <div className="mb-3 flex items-center gap-3 px-1">
              <Avatar name={user.fullName} size="md" />
              <div className="leading-tight">
                <p className="text-sm font-medium text-gray-900">{user.fullName}</p>
                <p className="text-xs text-gray-500">{user.roles.map((role) => ROLE_LABELS[role]).join(' · ')}</p>
              </div>
            </div>
          )}
          <nav className="flex flex-col gap-1" aria-label="Principal (móvil)">
            {visible.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                onClick={() => setOpen(false)}
                className={({ isActive }) => linkClasses(isActive, true)}
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
          <div className="mt-3 border-t border-gray-100 pt-3">
            <Button variant="outline" size="sm" fullWidth onClick={onLogout} loading={loggingOut}>
              Cerrar sesión
            </Button>
          </div>
        </div>
      )}
    </header>
  )
}
