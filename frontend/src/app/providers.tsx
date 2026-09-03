import type { ReactNode } from 'react'
import { SessionBootstrap } from './SessionBootstrap'

/** Application-wide providers. Zustand needs no provider; this hosts session bootstrap and future contexts. */
export function Providers({ children }: { children: ReactNode }) {
  return <SessionBootstrap>{children}</SessionBootstrap>
}
