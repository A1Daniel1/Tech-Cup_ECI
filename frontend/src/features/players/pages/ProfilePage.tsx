import { useState } from 'react'
import { Link } from 'react-router'
import { Avatar } from '@/components/atoms/Avatar'
import { Button } from '@/components/atoms/Button'
import { FileInput } from '@/components/atoms/FileInput'
import { Alert } from '@/components/molecules/Alert'
import { Card } from '@/components/molecules/Card'
import { PageHeader } from '@/components/molecules/PageHeader'
import { QueryState } from '@/components/molecules/QueryState'
import { useAuth } from '@/features/auth/hooks/useAuth'
import { useMutation } from '@/lib/useQuery'
import { useAuthStore } from '@/store/auth.store'
import { toast } from '@/store/ui.store'
import type { UpdateUserRequest, UpsertProfileRequest } from '@/types/api'
import { playersApi } from '../api'
import { BasicInfoForm } from '../components/BasicInfoForm'
import { ProfileForm } from '../components/ProfileForm'
import { useMyProfile } from '../hooks/usePlayers'

const MAX_PHOTO_BYTES = 5 * 1024 * 1024

export function ProfilePage() {
  const { user, hasRole, refreshMe } = useAuth()
  const isPlayer = hasRole('PLAYER')
  const profileQuery = useMyProfile(isPlayer)
  const profile = profileQuery.data
  const [photo, setPhoto] = useState<File | null>(null)
  const [photoError, setPhotoError] = useState<string | null>(null)

  const inTeam = !!(profile?.teamId ?? user?.teamId)

  const saveProfile = useMutation(async (payload: UpsertProfileRequest) => {
    const saved = await playersApi.upsertMyProfile(payload)
    profileQuery.setData(saved)
    await refreshMe().catch(() => null)
    return saved
  })

  const uploadPhoto = useMutation(async (file: File) => {
    const saved = await playersApi.uploadMyPhoto(file)
    profileQuery.setData(saved)
    return saved
  })

  const saveBasicInfo = useMutation(async (payload: UpdateUserRequest) => {
    if (!user) throw new Error('Sesión no disponible.')
    const updated = await playersApi.updateUser(user.id, payload)
    useAuthStore.setState({ user: updated })
    return updated
  })

  const handleSaveProfile = (payload: UpsertProfileRequest) => {
    saveProfile
      .mutate(payload)
      .then(() => toast.success(profile ? 'Perfil deportivo actualizado.' : 'Perfil deportivo creado.'))
      .catch(() => undefined)
  }

  const handlePhotoChange = (file: File | null) => {
    setPhotoError(null)
    if (file && file.size > MAX_PHOTO_BYTES) {
      setPhotoError('La foto no puede superar 5 MB.')
      setPhoto(null)
      return
    }
    setPhoto(file)
  }

  const handleUploadPhoto = () => {
    if (!photo) return
    uploadPhoto
      .mutate(photo)
      .then(() => {
        toast.success('Foto actualizada.')
        setPhoto(null)
      })
      .catch(() => undefined)
  }

  const handleSaveBasicInfo = (payload: UpdateUserRequest) => {
    saveBasicInfo
      .mutate(payload)
      .then(() => toast.success('Información actualizada.'))
      .catch(() => undefined)
  }

  return (
    <>
      <PageHeader title="Mi perfil" description="Información básica y perfil deportivo." />
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        <div className="flex flex-col gap-6 lg:col-span-2">
          {isPlayer ? (
            <QueryState loading={profileQuery.loading} error={profileQuery.error} onRetry={profileQuery.refetch}>
              <Card
                title="Perfil deportivo"
                description={
                  profile
                    ? 'Posición y dorsal con los que participa en el torneo.'
                    : 'Cree su perfil deportivo para poder unirse a un equipo.'
                }
              >
                {inTeam && (
                  <Alert kind="info" className="mb-4">
                    Su perfil deportivo no se puede modificar mientras pertenece a un equipo
                    {profile?.teamName ? ` (${profile.teamName})` : ''}. Si necesita cambiar su posición o dorsal,
                    comuníquese con su capitán.
                  </Alert>
                )}
                <ProfileForm
                  key={profile ? `${profile.position}-${profile.jerseyNumber}` : 'new'}
                  profile={profile}
                  disabled={inTeam}
                  loading={saveProfile.loading}
                  error={saveProfile.error}
                  fieldErrors={saveProfile.fieldErrors}
                  onSubmit={handleSaveProfile}
                />
              </Card>

              {profile && (
                <Card title="Foto de perfil" description="Imagen visible en la plantilla de su equipo.">
                  <div className="flex flex-col gap-4 sm:flex-row sm:items-center">
                    <Avatar name={profile.fullName} photoFileId={profile.photoFileId} size="xl" />
                    <div className="flex flex-1 flex-col gap-2">
                      <FileInput
                        accept="image/*"
                        value={photo}
                        onChange={handlePhotoChange}
                        disabled={inTeam}
                        invalid={!!photoError}
                        hint="PNG o JPG, máximo 5 MB"
                      />
                      {(photoError || uploadPhoto.error) && (
                        <p className="text-xs font-medium text-red-600">{photoError ?? uploadPhoto.error}</p>
                      )}
                      <div>
                        <Button
                          size="sm"
                          variant="outline"
                          disabled={!photo || inTeam}
                          loading={uploadPhoto.loading}
                          onClick={handleUploadPhoto}
                        >
                          Subir foto
                        </Button>
                      </div>
                    </div>
                  </div>
                </Card>
              )}
            </QueryState>
          ) : (
            <Card title="Perfil deportivo">
              <p className="text-sm text-gray-600">
                Solo los usuarios con rol de jugador tienen perfil deportivo. Si desea participar como jugador,
                contacte al organizador del torneo.
              </p>
            </Card>
          )}
        </div>

        <div className="flex flex-col gap-6">
          {user && (
            <Card title="Información básica">
              <BasicInfoForm
                user={user}
                loading={saveBasicInfo.loading}
                error={saveBasicInfo.error}
                fieldErrors={saveBasicInfo.fieldErrors}
                onSubmit={handleSaveBasicInfo}
              />
            </Card>
          )}
          {isPlayer && !inTeam && (
            <Card title="Siguiente paso">
              <p className="text-sm text-gray-600">
                {profile
                  ? 'Ya tiene perfil deportivo. Busque un equipo y envíe su solicitud de vinculación.'
                  : 'Cree su perfil deportivo para poder solicitar unirse a un equipo.'}
              </p>
              {profile && (
                <Link to="/teams" className="mt-3 inline-block">
                  <Button size="sm">Ver equipos</Button>
                </Link>
              )}
            </Card>
          )}
        </div>
      </div>
    </>
  )
}
