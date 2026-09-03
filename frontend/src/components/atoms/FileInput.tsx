import { useId, useRef, type ChangeEvent } from 'react'
import { cn } from '@/lib/cn'

export interface FileInputProps {
  id?: string
  name?: string
  accept?: string
  disabled?: boolean
  invalid?: boolean
  value: File | null
  onChange: (file: File | null) => void
  /** Helper shown when no file is selected. */
  hint?: string
  className?: string
}

function formatSize(bytes: number): string {
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(0)} KB`
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
}

export function FileInput({
  id,
  name,
  accept,
  disabled,
  invalid,
  value,
  onChange,
  hint = 'Seleccione un archivo',
  className,
}: FileInputProps) {
  const generatedId = useId()
  const inputId = id ?? generatedId
  const inputRef = useRef<HTMLInputElement>(null)

  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    onChange(event.target.files?.[0] ?? null)
  }

  const clear = () => {
    if (inputRef.current) inputRef.current.value = ''
    onChange(null)
  }

  return (
    <div
      className={cn(
        'flex items-center gap-3 rounded-xl border border-dashed bg-white px-3.5 py-3 text-sm',
        invalid ? 'border-red-400' : 'border-gray-300',
        disabled && 'opacity-60',
        className,
      )}
    >
      <label
        htmlFor={inputId}
        className={cn(
          'shrink-0 cursor-pointer rounded-lg bg-gray-100 px-3 py-1.5 font-medium text-gray-800 hover:bg-gray-200',
          disabled && 'pointer-events-none',
        )}
      >
        Elegir archivo
      </label>
      <input
        ref={inputRef}
        id={inputId}
        name={name}
        type="file"
        accept={accept}
        disabled={disabled}
        onChange={handleChange}
        className="sr-only"
      />
      <span className="min-w-0 flex-1 truncate text-gray-600">
        {value ? (
          <>
            <span className="font-medium text-gray-900">{value.name}</span>{' '}
            <span className="text-gray-500">({formatSize(value.size)})</span>
          </>
        ) : (
          hint
        )}
      </span>
      {value && !disabled && (
        <button type="button" onClick={clear} className="shrink-0 text-xs text-gray-500 hover:text-red-600">
          Quitar
        </button>
      )}
    </div>
  )
}
