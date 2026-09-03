import { useQuery } from '@/lib/useQuery'
import { teamsApi } from '../api'

export function useTeams() {
  return useQuery((signal) => teamsApi.list(signal), [])
}

export function useTeam(id: number | null) {
  return useQuery((signal) => teamsApi.get(id as number, signal), [id], { enabled: id !== null })
}

/** The viewer's team. Resolves to `null` when the backend answers 404. */
export function useMyTeam() {
  return useQuery((signal) => teamsApi.mine(signal), [], { nullOnStatus: [404] })
}

export function useEligibility(teamId: number | null) {
  return useQuery((signal) => teamsApi.eligibility(teamId as number, signal), [teamId], { enabled: teamId !== null })
}
