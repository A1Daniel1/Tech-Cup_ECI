import { Button } from '@/components/atoms/Button'
import { Card } from '@/components/molecules/Card'
import { QueryState } from '@/components/molecules/QueryState'
import { JoinRequestList } from '@/features/players/components/JoinRequestList'
import type { JoinRequestResponse } from '@/types/api'

export interface JoinRequestsPanelProps {
  requests: JoinRequestResponse[] | null
  loading: boolean
  error: string | null
  onRetry?: () => void
  /** Id of the request currently being processed. */
  busyId: number | null
  onAccept: (request: JoinRequestResponse) => void
  onReject: (request: JoinRequestResponse) => void
}

/** Captain view of pending join requests. */
export function JoinRequestsPanel({ requests, loading, error, onRetry, busyId, onAccept, onReject }: JoinRequestsPanelProps) {
  return (
    <Card title="Solicitudes de vinculación" description="Jugadores que desean unirse al equipo." padded={false}>
      <div className="p-4">
        <QueryState loading={loading} error={error} onRetry={onRetry} inline>
          <JoinRequestList
            requests={requests ?? []}
            perspective="captain"
            emptyTitle="Sin solicitudes pendientes"
            emptyDescription="Cuando un jugador solicite unirse, aparecerá aquí."
            renderActions={(request) => (
              <>
                <Button size="sm" onClick={() => onAccept(request)} loading={busyId === request.id} disabled={busyId !== null}>
                  Aceptar
                </Button>
                <Button
                  size="sm"
                  variant="outline"
                  onClick={() => onReject(request)}
                  disabled={busyId !== null}
                >
                  Rechazar
                </Button>
              </>
            )}
          />
        </QueryState>
      </div>
    </Card>
  )
}
