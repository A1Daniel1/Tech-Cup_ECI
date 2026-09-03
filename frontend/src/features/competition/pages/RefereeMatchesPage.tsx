import { Link } from 'react-router'
import { Button } from '@/components/atoms/Button'
import { EmptyState } from '@/components/molecules/EmptyState'
import { PageHeader } from '@/components/molecules/PageHeader'
import { QueryState } from '@/components/molecules/QueryState'
import { MatchCard } from '@/components/organisms/MatchCard'
import { MATCH_PHASES } from '@/types/api'
import { SanctionedPlayersPanel } from '../components/SanctionedPlayersPanel'
import { useRefereeMatches } from '../hooks/useCompetition'

export function RefereeMatchesPage() {
  const { data, loading, error, refetch } = useRefereeMatches()

  const sorted = [...(data ?? [])].sort((a, b) => {
    const aTime = a.scheduledAt ?? ''
    const bTime = b.scheduledAt ?? ''
    if (aTime !== bTime) return aTime.localeCompare(bTime)
    return MATCH_PHASES.indexOf(a.phase) - MATCH_PHASES.indexOf(b.phase)
  })

  return (
    <>
      <PageHeader
        title="Arbitraje"
        description="Partidos asignados y jugadores sancionados para cada uno."
      />
      <QueryState loading={loading} error={error} onRetry={refetch}>
        {sorted.length === 0 ? (
          <EmptyState
            title="Sin partidos asignados"
            description="Cuando el organizador le asigne un partido, aparecerá en esta lista."
          />
        ) : (
          <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
            {sorted.map((match) => (
              <MatchCard
                key={match.id}
                match={match}
                actions={
                  <div className="flex w-full flex-col gap-3">
                    <div>
                      <Link to={`/matches/${match.id}`}>
                        <Button size="sm" variant="ghost">
                          Ver partido
                        </Button>
                      </Link>
                    </div>
                    <SanctionedPlayersPanel matchId={match.id} />
                  </div>
                }
              />
            ))}
          </div>
        )}
      </QueryState>
    </>
  )
}
