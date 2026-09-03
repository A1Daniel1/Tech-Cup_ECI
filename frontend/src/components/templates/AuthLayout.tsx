import type { ReactNode } from 'react'
import { Link } from 'react-router'
import { ToastViewport } from '@/components/molecules/ToastViewport'

export interface AuthLayoutProps {
  title: string
  subtitle?: ReactNode
  children: ReactNode
  /** Rendered below the card (e.g. "¿No tiene cuenta?"). */
  footer?: ReactNode
  wide?: boolean
}

/** Public layout for login / register. */
export function AuthLayout({ title, subtitle, children, footer, wide }: AuthLayoutProps) {
  return (
    <div className="flex min-h-screen flex-col bg-gray-50">
      <div className="flex flex-1 flex-col items-center justify-center px-4 py-10">
        <Link to="/" className="mb-6 flex items-center gap-2 text-gray-900">
          <span className="flex h-10 w-10 items-center justify-center rounded-xl bg-emerald-600 text-white">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" className="h-6 w-6" aria-hidden="true">
              <circle cx="12" cy="12" r="9" />
              <path d="M12 7.5l3.2 2.3-1.2 3.8H10l-1.2-3.8L12 7.5z" />
            </svg>
          </span>
          <span className="text-xl font-semibold tracking-tight">
            TechCup <span className="text-emerald-600">Fútbol</span>
          </span>
        </Link>
        <div className={`w-full rounded-2xl border border-gray-200 bg-white p-6 shadow-sm sm:p-8 ${wide ? 'max-w-2xl' : 'max-w-md'}`}>
          <h1 className="text-xl font-semibold text-gray-900">{title}</h1>
          {subtitle && <p className="mt-1 text-sm text-gray-500">{subtitle}</p>}
          <div className="mt-6">{children}</div>
        </div>
        {footer && <div className="mt-4 text-sm text-gray-600">{footer}</div>}
      </div>
      <ToastViewport />
    </div>
  )
}
