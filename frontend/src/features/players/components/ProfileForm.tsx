import { useState, type FormEvent } from 'react'
import { Button } from '@/components/atoms/Button'
import { Input } from '@/components/atoms/Input'
import { Select } from '@/components/atoms/Select'
import { Alert } from '@/components/molecules/Alert'
import { FormField } from '@/components/molecules/FormField'
import { POSITION_LABELS, toOptions } from '@/lib/labels'
import { POSITIONS, type PlayerProfileResponse, type Position, type UpsertProfileRequest } from '@/types/api'

export interface ProfileFormProps {
  profile: PlayerProfileResponse | null
  disabled: boolean
  loading: boolean
  error: string | null
  fieldErrors: Record<string, string>
  onSubmit: (payload: UpsertProfileRequest) => void
}

const POSITION_OPTIONS = toOptions(POSITIONS, POSITION_LABELS)

/** Controlled by initial props only; the page remounts it (via `key`) when the profile changes. */
export function ProfileForm({ profile, disabled, loading, error, fieldErrors, onSubmit }: ProfileFormProps) {
  const [position, setPosition] = useState<Position | ''>(profile?.position ?? '')
  const [jerseyNumber, setJerseyNumber] = useState(profile ? String(profile.jerseyNumber) : '')
  const [localErrors, setLocalErrors] = useState<{ position?: string; jerseyNumber?: string }>({})

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    const errors: { position?: string; jerseyNumber?: string } = {}
    const number = Number(jerseyNumber)
    if (!position) errors.position = 'Seleccione una posición.'
    if (!jerseyNumber.trim() || !Number.isInteger(number) || number < 1 || number > 99) {
      errors.jerseyNumber = 'Ingrese un dorsal entre 1 y 99.'
    }
    setLocalErrors(errors)
    if (Object.keys(errors).length > 0 || !position) return
    onSubmit({ position, jerseyNumber: number })
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4" noValidate>
      {error && <Alert kind="error">{error}</Alert>}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <FormField label="Posición" required error={localErrors.position ?? fieldErrors.position}>
          <Select
            options={POSITION_OPTIONS}
            placeholder="Seleccione"
            value={position}
            disabled={disabled}
            onChange={(event) => setPosition(event.target.value as Position | '')}
          />
        </FormField>
        <FormField label="Número de dorsal" required error={localErrors.jerseyNumber ?? fieldErrors.jerseyNumber} hint="Entre 1 y 99.">
          <Input
            type="number"
            min={1}
            max={99}
            inputMode="numeric"
            value={jerseyNumber}
            disabled={disabled}
            onChange={(event) => setJerseyNumber(event.target.value)}
          />
        </FormField>
      </div>
      <div>
        <Button type="submit" loading={loading} disabled={disabled}>
          {profile ? 'Guardar cambios' : 'Crear perfil deportivo'}
        </Button>
      </div>
    </form>
  )
}
