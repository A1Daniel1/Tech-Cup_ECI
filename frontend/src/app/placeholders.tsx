import { Link } from 'react-router'
import { Button } from '@/components/atoms/Button'
import { EmptyState } from '@/components/molecules/EmptyState'

export function NotFoundPage() {
  return (
    <EmptyState
      title="Página no encontrada"
      description="La dirección que ingresó no existe o fue movida."
      action={
        <Link to="/">
          <Button size="sm" variant="outline">
            Ir al inicio
          </Button>
        </Link>
      }
    />
  )
}
