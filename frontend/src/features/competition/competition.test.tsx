import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import type { MatchPhase, MatchResponse, TeamMember } from '@/types/api'
import { LineupEditor } from './components/LineupEditor'
import { ResultForm } from './components/ResultForm'
import {
  EMPTY_RESULT_VALUES,
  LINEUP_STARTERS,
  toResultRequest,
  validateLineup,
  validateResult,
  type ResultEventDraft,
  type ResultFormValues,
} from './validation'

// ---------------------------------------------------------------------------
// Fixtures
// ---------------------------------------------------------------------------

const HOME_TEAM = { id: 10, name: 'Leones FC', colors: 'Rojo y negro' }
const AWAY_TEAM = { id: 20, name: 'Aguilas FC', colors: 'Azul' }

function makeMatch(phase: MatchPhase = 'GROUP'): MatchResponse {
  return {
    id: 7,
    tournamentId: 1,
    phase,
    roundNumber: 1,
    homeTeam: HOME_TEAM,
    awayTeam: AWAY_TEAM,
    venue: { id: 1, name: 'Cancha norte' },
    referee: { id: 99, fullName: 'Ana Ruiz' },
    scheduledAt: '2026-03-10T23:00:00Z',
    status: 'SCHEDULED',
    homeScore: null,
    awayScore: null,
    homePenalties: null,
    awayPenalties: null,
    cancelReason: null,
    events: [],
  }
}

function goal(key: string, side: 'home' | 'away', playerId: string): ResultEventDraft {
  return { key, side, playerId, type: 'GOAL', minute: '' }
}

function values(overrides: Partial<ResultFormValues> = {}): ResultFormValues {
  return { ...EMPTY_RESULT_VALUES, ...overrides }
}

function member(userId: number, fullName: string, jerseyNumber: number, position: TeamMember['position']): TeamMember {
  return {
    userId,
    fullName,
    position,
    jerseyNumber,
    academicProgram: 'SYSTEMS_ENGINEERING',
    photoFileId: null,
  }
}

const HOME_ROSTER: TeamMember[] = [
  member(1, 'Camilo Rivas', 1, 'GOALKEEPER'),
  member(2, 'Daniel Soto', 2, 'DEFENDER'),
]
const AWAY_ROSTER: TeamMember[] = [
  member(11, 'Mateo Vargas', 11, 'FORWARD'),
  member(12, 'Nicolas Pardo', 12, 'MIDFIELDER'),
]

// ---------------------------------------------------------------------------
// validateResult
// ---------------------------------------------------------------------------

describe('validateResult', () => {
  it('accepts a result whose goal events match the score of each team', () => {
    const result = validateResult(
      values({
        homeScore: '2',
        awayScore: '1',
        events: [goal('a', 'home', '1'), goal('b', 'home', '2'), goal('c', 'away', '11')],
      }),
      'GROUP',
    )
    expect(result.valid).toBe(true)
    expect(result.errors).toEqual({})
  })

  it('rejects a score with more goals than registered events', () => {
    const result = validateResult(values({ homeScore: '2', awayScore: '0', events: [goal('a', 'home', '1')] }), 'GROUP')
    expect(result.valid).toBe(false)
    expect(result.errors.homeScore).toContain('(1)')
    expect(result.errors.homeScore).toContain('(2)')
    expect(result.errors.awayScore).toBeUndefined()
  })

  it('rejects goal events registered for the wrong team', () => {
    const result = validateResult(values({ homeScore: '1', awayScore: '0', events: [goal('a', 'away', '11')] }), 'GROUP')
    expect(result.valid).toBe(false)
    expect(result.errors.homeScore).toBeDefined()
    expect(result.errors.awayScore).toBeDefined()
  })

  it('ignores card events when counting goals', () => {
    const result = validateResult(
      values({
        homeScore: '1',
        awayScore: '0',
        events: [goal('a', 'home', '1'), { key: 'b', side: 'home', playerId: '2', type: 'YELLOW_CARD', minute: '35' }],
      }),
      'GROUP',
    )
    expect(result.valid).toBe(true)
  })

  it('requires a complete event row', () => {
    const result = validateResult(
      values({ homeScore: '0', awayScore: '0', events: [{ key: 'a', side: 'home', playerId: '', type: '', minute: '' }] }),
      'GROUP',
    )
    expect(result.valid).toBe(false)
    expect(result.eventErrors.a).toBe('Seleccione el jugador.')
  })

  it('rejects penalties in the group stage and demands them for a knockout draw', () => {
    const groupWithPenalties = validateResult(
      values({ homeScore: '0', awayScore: '0', homePenalties: '3', awayPenalties: '1' }),
      'GROUP',
    )
    expect(groupWithPenalties.errors.homePenalties).toBeDefined()

    const knockoutDraw = validateResult(values({ homeScore: '1', awayScore: '1', events: [goal('a', 'home', '1'), goal('b', 'away', '11')] }), 'FINAL')
    expect(knockoutDraw.valid).toBe(false)
    expect(knockoutDraw.errors.homePenalties).toBeDefined()

    const decided = validateResult(
      values({
        homeScore: '1',
        awayScore: '1',
        homePenalties: '4',
        awayPenalties: '2',
        events: [goal('a', 'home', '1'), goal('b', 'away', '11')],
      }),
      'FINAL',
    )
    expect(decided.valid).toBe(true)
  })

  it('maps validated values to the API payload', () => {
    const payload = toResultRequest(
      values({
        homeScore: '1',
        awayScore: '0',
        homePenalties: '',
        awayPenalties: '',
        events: [{ key: 'a', side: 'home', playerId: '2', type: 'GOAL', minute: '12' }],
      }),
      { home: HOME_TEAM.id, away: AWAY_TEAM.id },
      'GROUP',
    )
    expect(payload).toEqual({
      homeScore: 1,
      awayScore: 0,
      events: [{ teamId: HOME_TEAM.id, playerId: 2, type: 'GOAL', minute: 12 }],
    })
  })
})

// ---------------------------------------------------------------------------
// ResultForm
// ---------------------------------------------------------------------------

describe('ResultForm', () => {
  function renderForm(phase: MatchPhase = 'GROUP') {
    const onSubmit = vi.fn()
    render(
      <ResultForm
        match={makeMatch(phase)}
        rosters={{ home: HOME_ROSTER, away: AWAY_ROSTER }}
        loading={false}
        error={null}
        fieldErrors={{}}
        onSubmit={onSubmit}
      />,
    )
    return { onSubmit }
  }

  it('blocks the submission when the goals do not match the score', async () => {
    const user = userEvent.setup()
    const { onSubmit } = renderForm()

    await user.type(screen.getByLabelText(/^Goles de Leones FC/), '1')
    await user.type(screen.getByLabelText(/^Goles de Aguilas FC/), '0')
    await user.click(screen.getByRole('button', { name: 'Agregar evento' }))
    await user.selectOptions(screen.getByLabelText('Equipo'), 'away')
    await user.selectOptions(screen.getByLabelText('Jugador'), '11')
    await user.selectOptions(screen.getByLabelText('Tipo'), 'GOAL')
    await user.click(screen.getByRole('button', { name: 'Registrar resultado' }))

    expect(onSubmit).not.toHaveBeenCalled()
    expect(screen.getAllByText(/no coinciden con el marcador/)).toHaveLength(2)
  })

  it('submits the payload once every goal is assigned to the right team', async () => {
    const user = userEvent.setup()
    const { onSubmit } = renderForm()

    await user.type(screen.getByLabelText(/^Goles de Leones FC/), '1')
    await user.type(screen.getByLabelText(/^Goles de Aguilas FC/), '0')
    await user.click(screen.getByRole('button', { name: 'Agregar evento' }))
    await user.selectOptions(screen.getByLabelText('Equipo'), 'home')
    await user.selectOptions(screen.getByLabelText('Jugador'), '2')
    await user.selectOptions(screen.getByLabelText('Tipo'), 'GOAL')
    await user.type(screen.getByLabelText('Minuto'), '12')
    await user.click(screen.getByRole('button', { name: 'Registrar resultado' }))

    expect(onSubmit).toHaveBeenCalledTimes(1)
    expect(onSubmit).toHaveBeenCalledWith({
      homeScore: 1,
      awayScore: 0,
      events: [{ teamId: HOME_TEAM.id, playerId: 2, type: 'GOAL', minute: 12 }],
    })
  })

  it('only offers the penalties inputs in knockout phases', () => {
    renderForm('GROUP')
    expect(screen.queryByLabelText(/^Penales de Leones FC/)).not.toBeInTheDocument()
  })
})

// ---------------------------------------------------------------------------
// Lineup
// ---------------------------------------------------------------------------

describe('validateLineup', () => {
  it('requires exactly seven starters', () => {
    expect(validateLineup([1, 2, 3, 4, 5, 6, 7])).toBeUndefined()
    expect(validateLineup([1, 2, 3, 4, 5, 6])).toContain(`exactamente ${LINEUP_STARTERS} titulares`)
    expect(validateLineup([1, 2, 3, 4, 5, 6, 7, 8])).toContain('(seleccionados: 8)')
    expect(validateLineup([])).toBeDefined()
  })

  it('rejects repeated players', () => {
    expect(validateLineup([1, 1, 2, 3, 4, 5, 6])).toBe('La alineación tiene jugadores repetidos.')
  })
})

const ROSTER: TeamMember[] = [
  member(1, 'Camilo Rivas', 1, 'GOALKEEPER'),
  member(2, 'Daniel Soto', 2, 'DEFENDER'),
  member(3, 'Esteban Luna', 3, 'DEFENDER'),
  member(4, 'Felipe Mora', 4, 'MIDFIELDER'),
  member(5, 'Gabriel Nieto', 5, 'MIDFIELDER'),
  member(6, 'Hugo Pena', 6, 'MIDFIELDER'),
  member(7, 'Ivan Quintero', 7, 'FORWARD'),
  member(8, 'Julian Ramos', 8, 'FORWARD'),
]

describe('LineupEditor', () => {
  it('refuses to save with fewer than seven starters and saves once seven are picked', async () => {
    const user = userEvent.setup()
    const onSubmit = vi.fn()
    render(<LineupEditor members={ROSTER} loading={false} error={null} onSubmit={onSubmit} />)

    const checkboxes = screen.getAllByRole('checkbox')
    expect(checkboxes).toHaveLength(8)

    for (const checkbox of checkboxes.slice(0, 6)) await user.click(checkbox)
    expect(screen.getByText(`6 / ${LINEUP_STARTERS} seleccionados`)).toBeInTheDocument()

    await user.click(screen.getByRole('button', { name: 'Guardar alineación' }))
    expect(onSubmit).not.toHaveBeenCalled()
    expect(screen.getByRole('alert')).toHaveTextContent('exactamente 7 titulares (seleccionados: 6)')

    const seventh = checkboxes[6]
    expect(seventh).toBeDefined()
    await user.click(seventh as HTMLElement)

    // The eighth option is blocked once the lineup is complete.
    expect(checkboxes[7]).toBeDisabled()

    await user.click(screen.getByRole('button', { name: 'Guardar alineación' }))
    expect(onSubmit).toHaveBeenCalledWith({ formation: 'F_2_3_1', starterIds: [1, 2, 3, 4, 5, 6, 7] })
  })

  it('lets the captain change the formation before saving', async () => {
    const user = userEvent.setup()
    const onSubmit = vi.fn()
    render(<LineupEditor members={ROSTER} loading={false} error={null} onSubmit={onSubmit} />)

    await user.selectOptions(screen.getByLabelText('Formación'), 'F_3_2_1')
    for (const checkbox of screen.getAllByRole('checkbox').slice(0, 7)) await user.click(checkbox)
    await user.click(screen.getByRole('button', { name: 'Guardar alineación' }))

    expect(onSubmit).toHaveBeenCalledWith({ formation: 'F_3_2_1', starterIds: [1, 2, 3, 4, 5, 6, 7] })
  })
})
