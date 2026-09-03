import { useState, type FormEvent } from 'react'
import { Button } from '@/components/atoms/Button'
import { Input } from '@/components/atoms/Input'
import { Alert } from '@/components/molecules/Alert'
import { FormField } from '@/components/molecules/FormField'

export interface TeamFormValues {
  name: string
  colors: string
}

export interface TeamFormProps {
  initial?: TeamFormValues
  disabled?: boolean
  loading: boolean
  error: string | null
  fieldErrors: Record<string, string>
  submitLabel: string
  onSubmit: (values: TeamFormValues) => void
  onCancel?: () => void
}

export function TeamForm({
  initial = { name: '', colors: '' },
  disabled = false,
  loading,
  error,
  fieldErrors,
  submitLabel,
  onSubmit,
  onCancel,
}: TeamFormProps) {
  const [name, setName] = useState(initial.name)
  const [colors, setColors] = useState(initial.colors)
  const [localErrors, setLocalErrors] = useState<Partial<TeamFormValues>>({})

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    const errors: Partial<TeamFormValues> = {}
    if (name.trim().length < 3) errors.name = 'El nombre debe tener al menos 3 caracteres.'
    if (!colors.trim()) errors.colors = 'Indique los colores del equipo.'
    setLocalErrors(errors)
    if (Object.keys(errors).length > 0) return
    onSubmit({ name: name.trim(), colors: colors.trim() })
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4" noValidate>
      {error && <Alert kind="error">{error}</Alert>}
      <FormField label="Nombre del equipo" required error={localErrors.name ?? fieldErrors.name}>
        <Input value={name} disabled={disabled} onChange={(event) => setName(event.target.value)} />
      </FormField>
      <FormField
        label="Colores"
        required
        error={localErrors.colors ?? fieldErrors.colors}
        hint="Por ejemplo: verde y blanco."
      >
        <Input value={colors} disabled={disabled} onChange={(event) => setColors(event.target.value)} />
      </FormField>
      <div className="flex gap-2">
        <Button type="submit" loading={loading} disabled={disabled}>
          {submitLabel}
        </Button>
        {onCancel && (
          <Button type="button" variant="ghost" onClick={onCancel} disabled={loading}>
            Cancelar
          </Button>
        )}
      </div>
    </form>
  )
}
