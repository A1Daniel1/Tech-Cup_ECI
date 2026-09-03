import { useState } from 'react'
import { useNavigate } from 'react-router'
import { Button } from '@/components/atoms/Button'
import { EmptyState } from '@/components/molecules/EmptyState'
import { Modal } from '@/components/molecules/Modal'
import { PageHeader } from '@/components/molecules/PageHeader'
import { QueryState } from '@/components/molecules/QueryState'
import { useAuth } from '@/features/auth/hooks/useAuth'
import { useMutation } from '@/lib/useQuery'
import { toast } from '@/store/ui.store'
import { TOURNAMENT_STATUSES, type CreateTournamentRequest } from '@/types/api'
import { tournamentsApi } from '../api'
import { TournamentCard } from '../components/TournamentCard'
import { TournamentForm } from '../components/TournamentForm'
import { useCurrentTournament, useTournaments } from '../hooks/useTournaments'

export function TournamentsPage() {
  const { hasRole } = useAuth()
  const navigate = useNavigate()
  const isOrganizer = hasRole('ORGANIZER')
  const list = useTournaments()
  const current = useCurrentTournament()
  const [createOpen, setCreateOpen] = useState(false)

  const create = useMutation((payload: CreateTournamentRequest) => tournamentsApi.create(payload))

  const sorted = [...(list.data ?? [])].sort((a, b) => {
    const order = TOURNAMENT_STATUSES.indexOf(a.status) - TOURNAMENT_STATUSES.indexOf(b.status)
    // ACTIVE / IN_PROGRESS first, then DRAFT, then FINISHED; newest first within status.
    const rank = (status: typeof a.status) => (status === 'IN_PROGRESS' ? 0 : status === 'ACTIVE' ? 1 : status === 'DRAFT' ? 2 : 3)
    return rank(a.status) - rank(b.status) || order || b.startDate.localeCompare(a.startDate)
  })

  return (
    <>
      <PageHeader
        title="Torneos"
        description="Calendario, reglamento, tabla de posiciones y llaves de cada torneo."
        actions={
          isOrganizer && (
            <Button size="sm" onClick={() => setCreateOpen(true)}>
              Crear torneo
            </Button>
          )
        }
      />
      <QueryState loading={list.loading} error={list.error} onRetry={list.refetch}>
        {sorted.length === 0 ? (
          <EmptyState
            title="No hay torneos registrados"
            description={isOrganizer ? 'Cree el primer torneo del semestre.' : 'Cuando el organizador cree un torneo, aparecerá aquí.'}
            action={
              isOrganizer ? (
                <Button size="sm" onClick={() => setCreateOpen(true)}>
                  Crear torneo
                </Button>
              ) : undefined
            }
          />
        ) : (
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {sorted.map((tournament) => (
              <TournamentCard key={tournament.id} tournament={tournament} current={tournament.id === current.data?.id} />
            ))}
          </div>
        )}
      </QueryState>

      <Modal open={createOpen} onClose={() => setCreateOpen(false)} title="Crear torneo" description="El torneo se crea en estado borrador.">
        <TournamentForm
          loading={create.loading}
          error={create.error}
          fieldErrors={create.fieldErrors}
          submitLabel="Crear torneo"
          onCancel={() => setCreateOpen(false)}
          onSubmit={(payload) =>
            create
              .mutate(payload)
              .then((created) => {
                toast.success('Torneo creado en borrador.')
                setCreateOpen(false)
                navigate(`/tournaments/${created.id}?tab=manage`)
              })
              .catch(() => undefined)
          }
        />
      </Modal>
    </>
  )
}
