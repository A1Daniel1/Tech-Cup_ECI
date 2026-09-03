import { Link } from 'react-router'
import { Button } from '@/components/atoms/Button'
import { EmptyState } from '@/components/molecules/EmptyState'
import { PageHeader } from '@/components/molecules/PageHeader'
import { QueryState } from '@/components/molecules/QueryState'
import { useAuth } from '@/features/auth/hooks/useAuth'
import { TeamCard } from '../components/TeamCard'
import { useTeams } from '../hooks/useTeams'

export function TeamsPage() {
  const { user, hasRole } = useAuth()
  const { data, loading, error, refetch } = useTeams()
  const isCaptain = hasRole('CAPTAIN')
  const myTeamId = user?.teamId ?? null

  const teams = [...(data ?? [])].sort((a, b) => {
    if (a.status !== b.status) return a.status === 'ACTIVE' ? -1 : 1
    return a.name.localeCompare(b.name, 'es')
  })

  return (
    <>
      <PageHeader
        title="Equipos"
        description="Equipos registrados en la plataforma."
        actions={
          isCaptain && (
            <Link to="/my-team">
              <Button size="sm" variant={myTeamId ? 'outline' : 'primary'}>
                {myTeamId ? 'Gestionar mi equipo' : 'Crear equipo'}
              </Button>
            </Link>
          )
        }
      />
      <QueryState loading={loading} error={error} onRetry={refetch}>
        {teams.length === 0 ? (
          <EmptyState
            title="Aún no hay equipos"
            description={
              isCaptain
                ? 'Sea el primero en crear un equipo para el torneo.'
                : 'Cuando los capitanes creen sus equipos, aparecerán aquí.'
            }
            action={
              isCaptain ? (
                <Link to="/my-team">
                  <Button size="sm">Crear equipo</Button>
                </Link>
              ) : undefined
            }
          />
        ) : (
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {teams.map((team) => (
              <TeamCard key={team.id} team={team} isMine={team.id === myTeamId} />
            ))}
          </div>
        )}
      </QueryState>
    </>
  )
}
