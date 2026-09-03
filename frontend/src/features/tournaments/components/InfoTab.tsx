import { StatusBadge } from '@/components/atoms/Badge'
import { Card } from '@/components/molecules/Card'
import { StatTile } from '@/components/molecules/StatTile'
import { formatDate, formatMoney } from '@/lib/format'
import type { TournamentResponse } from '@/types/api'
import { RulebookLink } from './RulebookLink'
import { VenueGallery } from './VenueGallery'

export function InfoTab({ tournament }: { tournament: TournamentResponse }) {
  return (
    <div className="flex flex-col gap-6">
      <div className="grid grid-cols-2 gap-3 md:grid-cols-4">
        <StatTile label="Estado" value={<StatusBadge kind="tournament" value={tournament.status} size="md" />} />
        <StatTile label="Fecha inicial" value={<span className="text-base">{formatDate(tournament.startDate)}</span>} />
        <StatTile label="Fecha final" value={<span className="text-base">{formatDate(tournament.endDate)}</span>} />
        <StatTile
          label="Cierre de inscripciones"
          value={<span className="text-base">{formatDate(tournament.registrationDeadline)}</span>}
        />
        <StatTile label="Equipos aprobados" value={`${tournament.approvedTeams} / ${tournament.maxTeams}`} />
        <StatTile label="Costo de inscripción" value={<span className="text-base">{formatMoney(tournament.fee)}</span>} />
      </div>

      <Card title="Reglamento" description="Documento oficial con las reglas del torneo.">
        <RulebookLink fileId={tournament.rulebookFileId} tournamentName={tournament.name} />
      </Card>

      <Card title="Canchas" description="Escenarios donde se disputarán los partidos.">
        <VenueGallery venues={tournament.venues} />
      </Card>
    </div>
  )
}
