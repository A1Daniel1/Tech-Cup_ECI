import type { NavItem } from '@/components/organisms/Navbar'

/** Navigation entries with the roles allowed to see them. Empty roles = any authenticated user. */
export const NAV_ITEMS: NavItem[] = [
  { to: '/', label: 'Inicio', end: true },
  { to: '/tournaments', label: 'Torneos' },
  { to: '/teams', label: 'Equipos' },
  { to: '/profile', label: 'Mi perfil' },
  { to: '/my-requests', label: 'Mis solicitudes', roles: ['PLAYER'] },
  { to: '/my-team', label: 'Mi equipo', roles: ['CAPTAIN'] },
  { to: '/players', label: 'Jugadores', roles: ['CAPTAIN', 'ORGANIZER'] },
  { to: '/referee/matches', label: 'Arbitraje', roles: ['REFEREE'] },
  { to: '/admin/users', label: 'Usuarios', roles: ['ORGANIZER', 'ADMIN'] },
  { to: '/admin/audit', label: 'Auditoría', roles: ['ADMIN'] },
]
