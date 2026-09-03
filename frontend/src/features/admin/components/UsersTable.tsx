import { StatusBadge } from '@/components/atoms/Badge'
import { Button } from '@/components/atoms/Button'
import { Table, type Column } from '@/components/molecules/Table'
import { ACADEMIC_PROGRAM_LABELS, SCHOOL_RELATION_LABELS } from '@/lib/labels'
import type { UserResponse } from '@/types/api'

export interface UsersTableProps {
  users: UserResponse[]
  currentUserId: number | null
  canManageRoles: boolean
  canManageCaptains: boolean
  canInactivate: boolean
  busyUserId: number | null
  onManageRoles: (user: UserResponse) => void
  onToggleCaptain: (user: UserResponse) => void
  onInactivate: (user: UserResponse) => void
}

export function UsersTable({
  users,
  currentUserId,
  canManageRoles,
  canManageCaptains,
  canInactivate,
  busyUserId,
  onManageRoles,
  onToggleCaptain,
  onInactivate,
}: UsersTableProps) {
  const columns: Column<UserResponse>[] = [
    {
      key: 'user',
      header: 'Usuario',
      cell: (user) => (
        <div className="min-w-0">
          <p className="truncate font-medium text-gray-900">{user.fullName}</p>
          <p className="truncate text-xs text-gray-500">{user.email}</p>
        </div>
      ),
    },
    {
      key: 'relation',
      header: 'Vínculo',
      hideOnMobile: true,
      cell: (user) => (
        <div>
          <p>{SCHOOL_RELATION_LABELS[user.schoolRelation]}</p>
          <p className="text-xs text-gray-500">{ACADEMIC_PROGRAM_LABELS[user.academicProgram]}</p>
        </div>
      ),
    },
    {
      key: 'roles',
      header: 'Roles',
      cell: (user) => (
        <div className="flex flex-wrap gap-1">
          {user.roles.map((role) => (
            <StatusBadge key={role} kind="role" value={role} />
          ))}
        </div>
      ),
    },
    {
      key: 'status',
      header: 'Estado',
      hideOnMobile: true,
      cell: (user) => <StatusBadge kind="user" value={user.status} />,
    },
    {
      key: 'actions',
      header: 'Acciones',
      align: 'right',
      cell: (user) => {
        const busy = busyUserId === user.id
        const isSelf = user.id === currentUserId
        const isCaptain = user.roles.includes('CAPTAIN')
        const inactive = user.status === 'INACTIVE'
        return (
          <div className="flex flex-wrap justify-end gap-1.5">
            {canManageRoles && (
              <Button size="sm" variant="outline" onClick={() => onManageRoles(user)} disabled={busy}>
                Roles
              </Button>
            )}
            {canManageCaptains && !inactive && (
              <Button size="sm" variant={isCaptain ? 'ghost' : 'outline'} onClick={() => onToggleCaptain(user)} loading={busy}>
                {isCaptain ? 'Revocar capitán' : 'Otorgar capitán'}
              </Button>
            )}
            {canInactivate && !inactive && !isSelf && (
              <Button
                size="sm"
                variant="ghost"
                className="text-red-600 hover:bg-red-50"
                onClick={() => onInactivate(user)}
                disabled={busy}
              >
                Inactivar
              </Button>
            )}
          </div>
        )
      },
    },
  ]

  return <Table columns={columns} rows={users} rowKey={(user) => user.id} empty="No se encontraron usuarios." />
}
