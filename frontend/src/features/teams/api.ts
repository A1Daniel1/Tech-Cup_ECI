import { api } from '@/lib/api'
import type { CreateTeamRequest, EligibilityResponse, TeamResponse, UpdateTeamRequest } from '@/types/api'

export const teamsApi = {
  list: (signal?: AbortSignal) => api.get<TeamResponse[]>('/teams', undefined, signal),
  mine: (signal?: AbortSignal) => api.get<TeamResponse>('/teams/mine', undefined, signal),
  get: (id: number, signal?: AbortSignal) => api.get<TeamResponse>(`/teams/${id}`, undefined, signal),
  create: (payload: CreateTeamRequest) => api.post<TeamResponse>('/teams', payload),
  update: (id: number, payload: UpdateTeamRequest) => api.patch<TeamResponse>(`/teams/${id}`, payload),
  removeMember: (id: number, userId: number) => api.delete<TeamResponse>(`/teams/${id}/members/${userId}`),
  inactivate: (id: number) => api.post<TeamResponse>(`/teams/${id}/inactivate`),
  eligibility: (id: number, signal?: AbortSignal) =>
    api.get<EligibilityResponse>(`/teams/${id}/eligibility`, undefined, signal),
}
