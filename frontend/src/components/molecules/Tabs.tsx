import type { ReactNode } from 'react'
import { cn } from '@/lib/cn'

export interface TabItem<Id extends string = string> {
  id: Id
  label: ReactNode
  /** Small counter or badge rendered after the label. */
  badge?: ReactNode
}

export interface TabsProps<Id extends string = string> {
  tabs: TabItem<Id>[]
  active: Id
  onChange: (id: Id) => void
  className?: string
}

export function Tabs<Id extends string>({ tabs, active, onChange, className }: TabsProps<Id>) {
  return (
    <div className={cn('overflow-x-auto border-b border-gray-200', className)} role="tablist">
      <div className="flex min-w-max gap-1">
        {tabs.map((tab) => {
          const selected = tab.id === active
          return (
            <button
              key={tab.id}
              type="button"
              role="tab"
              aria-selected={selected}
              onClick={() => onChange(tab.id)}
              className={cn(
                '-mb-px flex items-center gap-1.5 whitespace-nowrap border-b-2 px-3 py-2.5 text-sm font-medium transition-colors',
                selected
                  ? 'border-emerald-600 text-emerald-700'
                  : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-800',
              )}
            >
              {tab.label}
              {tab.badge !== undefined && tab.badge !== null && (
                <span className="rounded-full bg-gray-100 px-1.5 text-xs text-gray-600">{tab.badge}</span>
              )}
            </button>
          )
        })}
      </div>
    </div>
  )
}
