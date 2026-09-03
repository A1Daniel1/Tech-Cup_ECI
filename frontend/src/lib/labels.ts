import type {
  AcademicProgram,
  CancelReason,
  DocumentType,
  EventType,
  Formation,
  InitialRole,
  JoinRequestStatus,
  MatchPhase,
  MatchStatus,
  Position,
  RegistrationStatus,
  Role,
  SchoolRelation,
  TeamStatus,
  TournamentStatus,
  UserStatus,
} from '@/types/api'

/** Spanish (es-CO, neutral register) labels for every enum value in the API contract. */

export const ROLE_LABELS: Record<Role, string> = {
  GUEST: 'Invitado',
  PLAYER: 'Jugador',
  CAPTAIN: 'Capitán',
  ORGANIZER: 'Organizador',
  REFEREE: 'Árbitro',
  ADMIN: 'Administrador',
}

export const INITIAL_ROLE_LABELS: Record<InitialRole, string> = {
  PLAYER: 'Jugador',
  GUEST: 'Invitado',
}

export const SCHOOL_RELATION_LABELS: Record<SchoolRelation, string> = {
  STUDENT: 'Estudiante',
  PROFESSOR: 'Profesor',
  ADMINISTRATIVE: 'Personal administrativo',
  GRADUATE: 'Graduado',
  FAMILY: 'Familiar',
}

export const ACADEMIC_PROGRAM_LABELS: Record<AcademicProgram, string> = {
  SYSTEMS_ENGINEERING: 'Ingeniería de Sistemas',
  AI_ENGINEERING: 'Ingeniería de Inteligencia Artificial',
  CYBERSECURITY_ENGINEERING: 'Ingeniería de Ciberseguridad',
  STATISTICS_ENGINEERING: 'Ingeniería Estadística',
  OTHER: 'Otro',
}

export const DOCUMENT_TYPE_LABELS: Record<DocumentType, string> = {
  CC: 'Cédula de ciudadanía',
  TI: 'Tarjeta de identidad',
  CE: 'Cédula de extranjería',
  PASSPORT: 'Pasaporte',
}

export const USER_STATUS_LABELS: Record<UserStatus, string> = {
  ACTIVE: 'Activo',
  INACTIVE: 'Inactivo',
}

export const POSITION_LABELS: Record<Position, string> = {
  GOALKEEPER: 'Portero',
  DEFENDER: 'Defensa',
  MIDFIELDER: 'Volante',
  FORWARD: 'Delantero',
}

export const TEAM_STATUS_LABELS: Record<TeamStatus, string> = {
  ACTIVE: 'Activo',
  INACTIVE: 'Inactivo',
}

export const JOIN_REQUEST_STATUS_LABELS: Record<JoinRequestStatus, string> = {
  PENDING: 'Pendiente',
  ACCEPTED: 'Aceptada',
  REJECTED: 'Rechazada',
  CANCELLED: 'Cancelada',
}

export const TOURNAMENT_STATUS_LABELS: Record<TournamentStatus, string> = {
  DRAFT: 'Borrador',
  ACTIVE: 'Activo',
  IN_PROGRESS: 'En progreso',
  FINISHED: 'Finalizado',
}

export const REGISTRATION_STATUS_LABELS: Record<RegistrationStatus, string> = {
  UNDER_REVIEW: 'En revisión',
  APPROVED: 'Aprobada',
  REJECTED: 'Rechazada',
  CANCELLED: 'Cancelada',
}

export const MATCH_PHASE_LABELS: Record<MatchPhase, string> = {
  GROUP: 'Fase de grupos',
  QUARTERFINAL: 'Cuartos de final',
  SEMIFINAL: 'Semifinal',
  FINAL: 'Final',
}

export const MATCH_STATUS_LABELS: Record<MatchStatus, string> = {
  SCHEDULED: 'Programado',
  PLAYED: 'Jugado',
  CANCELLED: 'Cancelado',
}

export const CANCEL_REASON_LABELS: Record<CancelReason, string> = {
  DISQUALIFIED: 'Descalificación',
  NO_SHOW: 'No presentación',
}

export const EVENT_TYPE_LABELS: Record<EventType, string> = {
  GOAL: 'Gol',
  YELLOW_CARD: 'Tarjeta amarilla',
  RED_CARD: 'Tarjeta roja',
}

export const FORMATION_LABELS: Record<Formation, string> = {
  F_3_2_1: '3-2-1',
  F_2_3_1: '2-3-1',
  F_4_1_1: '4-1-1',
  F_1_3_2: '1-3-2',
}

/** Human-readable labels for known audit actions. Unknown actions fall back to the raw value. */
export const AUDIT_ACTION_LABELS: Record<string, string> = {
  USER_REGISTERED: 'Registro de usuario',
  LOGIN: 'Inicio de sesión',
  LOGOUT: 'Cierre de sesión',
  USER_UPDATED: 'Actualización de usuario',
  USER_INACTIVATED: 'Inactivación de usuario',
  PROFILE_CREATED: 'Creación de perfil deportivo',
  PROFILE_UPDATED: 'Actualización de perfil deportivo',
  JOIN_REQUEST_ACCEPTED: 'Solicitud de vinculación aceptada',
  JOIN_REQUEST_REJECTED: 'Solicitud de vinculación rechazada',
  TEAM_CREATED: 'Creación de equipo',
  TEAM_UPDATED: 'Actualización de equipo',
  TEAM_MEMBER_REMOVED: 'Retiro de integrante',
  TEAM_INACTIVATED: 'Inactivación de equipo',
  TOURNAMENT_CREATED: 'Creación de torneo',
  TOURNAMENT_UPDATED: 'Actualización de torneo',
  TOURNAMENT_DELETED: 'Eliminación de torneo',
  TOURNAMENT_ACTIVATED: 'Activación de torneo',
  TOURNAMENT_STARTED: 'Inicio de torneo',
  TOURNAMENT_FINISHED: 'Finalización de torneo',
  REGISTRATION_CREATED: 'Inscripción creada',
  REGISTRATION_APPROVED: 'Inscripción aprobada',
  REGISTRATION_REJECTED: 'Inscripción rechazada',
  REGISTRATION_CANCELLED: 'Inscripción cancelada',
  MATCH_CREATED: 'Creación de partido',
  MATCH_UPDATED: 'Actualización de partido',
  MATCH_DELETED: 'Eliminación de partido',
  MATCH_RESULT_RECORDED: 'Resultado registrado',
}

export function auditActionLabel(action: string): string {
  return AUDIT_ACTION_LABELS[action] ?? action
}

/** Builds `{ value, label }` options from a label map, preserving the given key order. */
export function toOptions<K extends string>(
  keys: readonly K[],
  labels: Record<K, string>,
): { value: K; label: string }[] {
  return keys.map((value) => ({ value, label: labels[value] }))
}
