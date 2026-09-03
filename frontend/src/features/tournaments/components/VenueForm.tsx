import { useState, type FormEvent } from 'react'
import { Button } from '@/components/atoms/Button'
import { FileInput } from '@/components/atoms/FileInput'
import { Input } from '@/components/atoms/Input'
import { Textarea } from '@/components/atoms/Textarea'
import { Alert } from '@/components/molecules/Alert'
import { FormField } from '@/components/molecules/FormField'

export interface VenueFormValues {
  name: string
  description: string
  file: File | null
}

export interface VenueFormProps {
  loading: boolean
  error: string | null
  fieldErrors: Record<string, string>
  onSubmit: (values: VenueFormValues) => void
}

export function VenueForm({ loading, error, fieldErrors, onSubmit }: VenueFormProps) {
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [file, setFile] = useState<File | null>(null)
  const [nameError, setNameError] = useState<string | undefined>()

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    if (name.trim().length < 2) {
      setNameError('Ingrese el nombre de la cancha.')
      return
    }
    setNameError(undefined)
    onSubmit({ name: name.trim(), description: description.trim(), file })
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-3" noValidate>
      {error && <Alert kind="error">{error}</Alert>}
      <FormField label="Nombre" required error={nameError ?? fieldErrors.name}>
        <Input value={name} onChange={(event) => setName(event.target.value)} />
      </FormField>
      <FormField label="Descripción" error={fieldErrors.description}>
        <Textarea rows={2} value={description} onChange={(event) => setDescription(event.target.value)} />
      </FormField>
      <FormField label="Imagen" error={fieldErrors.file} hint="Opcional. PNG o JPG.">
        <FileInput accept="image/*" value={file} onChange={setFile} />
      </FormField>
      <div>
        <Button type="submit" size="sm" loading={loading}>
          Agregar cancha
        </Button>
      </div>
    </form>
  )
}
