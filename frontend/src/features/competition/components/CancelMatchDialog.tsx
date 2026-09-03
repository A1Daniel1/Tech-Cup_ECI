import { useState } from 'react'
import { Button } from '@/components/atoms/Button'
import { Select } from '@/components/atoms/Select'
import { Alert } from '@/components/molecules/Alert'
import { FormField } from '@/components/molecules/FormField'
import { Modal } from '@/components/molecules/Modal'
import { CANCEL_REASON_LABELS, toOptions } from '@/lib/labels'
import { CANCEL_REASONS, type CancelReason } from '@/types/api'

const REASON_OPTIONS = toOptions(CANCEL_REASONS, CANCEL_REASON_LABELS)

export interface CancelMatchDialogProps {
  open: boolean
  loading: boolean
  error: string | null
  onConfirm: (reason: CancelReason) => void
  onClose: () => void
}

/** Asks for the cancellation reason required by `DELETE /matches/{id}?reason=`. */
export function CancelMatchDialog({ open, loading, error, onConfirm, onClose }: CancelMatchDialogProps) {
  const [reason, setReason] = useState<CancelReason | ''>('')
  const [touched, setTouched] = useState(false)

  const handleConfirm = () => {
    setTouched(true)
    if (!reason) return
    onConfirm(reason)
  }

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Cancelar partido"
      description="El partido conserva su registro con estado Cancelado."
      size="sm"
      footer={
        <>
          <Button variant="outline" onClick={onClose} disabled={loading}>
            Volver
          </Button>
          <Button variant="danger" onClick={handleConfirm} loading={loading}>
            Cancelar partido
          </Button>
        </>
      }
    >
      {error && (
        <Alert kind="error" className="mb-3">
          {error}
        </Alert>
      )}
      <FormField label="Motivo" required error={touched && !reason ? 'Seleccione el motivo de la cancelación.' : undefined}>
        <Select
          options={REASON_OPTIONS}
          placeholder="Seleccione un motivo"
          value={reason}
          onChange={(event) => setReason(event.target.value as CancelReason | '')}
        />
      </FormField>
    </Modal>
  )
}
