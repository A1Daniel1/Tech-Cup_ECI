import { Link } from 'react-router'
import { StatusBadge } from '@/components/atoms/Badge'
import { cn } from '@/lib/cn'
import { formatDateTime } from '@/lib/format'
import { CANCEL_REASON_LABELS, MATCH_PHASE_LABELS } from '@/lib/labels'
import type { MatchResponse } from '@/types/api'
import { TeamColors } from './TeamColors'

export interface MatchScoreboardProps {
  match: MatchResponse
  /** Highlights this team (e.g. the viewer's team). */
  highlightTeamId?: number | null
}

function TeamBlock({
  team,
  highlighted,
  align,
}: {
  team: MatchResponse['homeTeam']
  highlighted: boolean
  align: 'left' | 'right'
}) {
  return (
    <div className={cn('flex min-w-0 flex-1 flex-col gap-1', align === 'right' ? 'items-end text-right' : 'items-start text-left')}>
      <Link
        to={`/teams/${team.id}`}
        className={cn('truncate text-base font-semibold hover:underline', highlighted ? 'text-emerald-700' : 'text-gray-900')}
      >
        {team.name}
      </Link>
      <TeamColors colors={team.colors} />
    </div>
  )
}

/** Big match header: teams with their colours, score, penalties and scheduling data. */
export function MatchScoreboard({ match, highlightTeamId }: MatchScoreboardProps) {
  const played = match.status === 'PLAYED' && match.homeScore !== null && match.awayScore !== null
  const hasPenalties = match.homePenalties !== null && match.awayPenalties !== null
  const roundLabel = match.phase === 'GROUP' ? `${MATCH_PHASE_LABELS.GROUP} · Jornada ${match.roundNumber}` : MATCH_PHASE_LABELS[match.phase]

  return (
    <section className="rounded-2xl border border-gray-200 bg-white p-5 shadow-sm">
      <div className="mb-4 flex flex-wrap items-center justify-between gap-2 text-xs text-gray-500">
        <span className="font-medium uppercase tracking-wide">{roundLabel}</span>
        <StatusBadge kind="match" value={match.status} size="md" />
      </div>

      <div className="flex items-center gap-4">
        <TeamBlock team={match.homeTeam} highlighted={match.homeTeam.id === highlightTeamId} align="right" />
        <div className="flex shrink-0 flex-col items-center">
          {played ? (
            <span className="rounded-xl bg-gray-900 px-4 py-2 text-2xl font-semibold tabular-nums text-white">
              {match.homeScore} – {match.awayScore}
            </span>
          ) : (
            <span className="rounded-xl bg-gray-100 px-4 py-2 text-lg font-semibold text-gray-500">vs</span>
          )}
          {hasPenalties && (
            <span className="mt-1 text-xs text-gray-500">
              Penales {match.homePenalties} – {match.awayPenalties}
            </span>
          )}
        </div>
        <TeamBlock team={match.awayTeam} highlighted={match.awayTeam.id === highlightTeamId} align="left" />
      </div>

      <dl className="mt-5 grid grid-cols-1 gap-3 border-t border-gray-100 pt-4 text-sm sm:grid-cols-3">
        <div>
          <dt className="text-xs uppercase tracking-wide text-gray-400">Fecha y hora</dt>
          <dd className="mt-0.5 text-gray-800">{formatDateTime(match.scheduledAt)}</dd>
        </div>
        <div>
          <dt className="text-xs uppercase tracking-wide text-gray-400">Cancha</dt>
          <dd className="mt-0.5 text-gray-800">{match.venue?.name ?? 'Por definir'}</dd>
        </div>
        <div>
          <dt className="text-xs uppercase tracking-wide text-gray-400">Árbitro</dt>
          <dd className="mt-0.5 text-gray-800">{match.referee?.fullName ?? 'Por definir'}</dd>
        </div>
      </dl>

      {match.status === 'CANCELLED' && match.cancelReason && (
        <p className="mt-3 text-sm text-red-600">Partido cancelado por {CANCEL_REASON_LABELS[match.cancelReason].toLowerCase()}.</p>
      )}
    </section>
  )
}
