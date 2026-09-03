import { useState } from 'react'
import { Link } from 'react-router'
import { Button } from '@/components/atoms/Button'
import { ConfirmDialog } from '@/components/molecules/Modal'
import { PageHeader } from '@/components/molecules/PageHeader'
import { QueryState } from '@/components/molecules/QueryState'
import { useMutation } from '@/lib/useQuery'
import { toast } from '@/store/ui.store'
import type { JoinRequestResponse } from '@/types/api'
import { playersApi } from '../api'
import { JoinRequestList } from '../components/JoinRequestList'
import { useMyJoinRequests } from '../hooks/usePlayers'

export function MyJoinRequestsPage() {
  const { data, loading, error, refetch, setData } = useMyJoinRequests()
  const [toCancel, setToCancel] = useState<JoinRequestResponse | null>(null)

  const cancel = useMutation(async (id: number) => {
    const updated = await playersApi.cancelJoinRequest(id)
    setData((previous) => previous?.map((request) => (request.id === id ? updated : request)) ?? null)
    return updated
  })

  const confirmCancel = () => {
    if (!toCancel) return
    cancel
      .mutate(toCancel.id)
      .then(() => {
        toast.success('Solicitud cancelada.')
        setToCancel(null)
      })
      .catch((cause: unknown) => {
        toast.error(cause instanceof Error ? cause.message : 'No fue posible cancelar la solicitud.')
        setToCancel(null)
      })
  }

  const sorted = [...(data ?? [])].sort((a, b) => b.createdAt.localeCompare(a.createdAt))

  return (
    <>
      <PageHeader
        title="Mis solicitudes"
        description="Solicitudes de vinculación enviadas a equipos. Solo puede tener una pendiente a la vez."
      />
      <QueryState loading={loading} error={error} onRetry={refetch}>
        <JoinRequestList
          requests={sorted}
          perspective="player"
          emptyTitle="No ha enviado solicitudes"
          emptyDescription="Explore los equipos disponibles y solicite unirse al que prefiera."
          emptyAction={
            <Link to="/teams">
              <Button size="sm">Ver equipos</Button>
            </Link>
          }
          renderActions={(request) =>
            request.status === 'PENDING' ? (
              <Button size="sm" variant="outline" onClick={() => setToCancel(request)}>
                Cancelar
              </Button>
            ) : null
          }
        />
      </QueryState>
      <ConfirmDialog
        open={toCancel !== null}
        title="Cancelar solicitud"
        description={toCancel ? `¿Desea cancelar la solicitud enviada a ${toCancel.teamName}?` : undefined}
        confirmLabel="Sí, cancelar"
        cancelLabel="Volver"
        danger
        loading={cancel.loading}
        onConfirm={confirmCancel}
        onCancel={() => setToCancel(null)}
      />
    </>
  )
}
