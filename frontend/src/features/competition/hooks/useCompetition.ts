import { adminApi } from '@/features/admin/api'
import { useQuery } from '@/lib/useQuery'
import { competitionApi } from '../api'

export function useMatch(id: number | null) {
  return useQuery((signal) => competitionApi.getMatch(id as number, signal), [id], { enabled: id !== null })
}

/** Lineup of a team for a match; `null` when the team has not registered one (404). */
export function useLineup(matchId: number | null, teamId: number | null) {
  return useQuery((signal) => competitionApi.getLineup(matchId as number, teamId as number, signal), [matchId, teamId], {
    enabled: matchId !== null && teamId !== null,
    nullOnStatus: [404],
  })
}

export function useRefereeMatches() {
  return useQuery((signal) => competitionApi.refereeMatches(signal), [])
}

export function useSanctionedPlayers(matchId: number | null, enabled = true) {
  return useQuery((signal) => competitionApi.sanctionedPlayers(matchId as number, signal), [matchId], {
    enabled: matchId !== null && enabled,
  })
}

/** Referees available to be assigned to a match. Organizer/admin only. */
export function useReferees(enabled = true) {
  return useQuery((signal) => adminApi.listReferees(signal), [], { enabled })
}
