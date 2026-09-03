import { useEffect, useState } from 'react'
import { fetchBlob } from '@/lib/api'

export interface FileUrlState {
  url: string | null
  /** MIME type reported by the server (e.g. `image/png`, `application/pdf`). */
  contentType: string | null
  loading: boolean
  error: string | null
}

interface LoadedFile {
  fileId: string
  url: string | null
  contentType: string | null
  error: string | null
}

/**
 * Fetches `GET /api/files/{id}` with the bearer token and exposes an object URL.
 * The URL is revoked when the id changes or the component unmounts.
 */
export function useFileUrl(fileId: string | null | undefined): FileUrlState {
  const [loaded, setLoaded] = useState<LoadedFile | null>(null)

  useEffect(() => {
    if (!fileId) return
    const controller = new AbortController()
    let objectUrl: string | null = null

    fetchBlob(`/files/${encodeURIComponent(fileId)}`, controller.signal)
      .then((blob) => {
        if (controller.signal.aborted) return
        objectUrl = URL.createObjectURL(blob)
        setLoaded({ fileId, url: objectUrl, contentType: blob.type || null, error: null })
      })
      .catch((cause: unknown) => {
        if (controller.signal.aborted) return
        setLoaded({
          fileId,
          url: null,
          contentType: null,
          error: cause instanceof Error ? cause.message : 'No fue posible cargar el archivo.',
        })
      })

    return () => {
      controller.abort()
      if (objectUrl) URL.revokeObjectURL(objectUrl)
    }
  }, [fileId])

  if (!fileId) return { url: null, contentType: null, loading: false, error: null }
  if (loaded && loaded.fileId === fileId) {
    return { url: loaded.url, contentType: loaded.contentType, loading: false, error: loaded.error }
  }
  return { url: null, contentType: null, loading: true, error: null }
}

/** Downloads a stored file with the bearer token and triggers a browser download. */
export async function downloadFile(fileId: string, filename: string): Promise<void> {
  const blob = await fetchBlob(`/files/${encodeURIComponent(fileId)}`)
  const url = URL.createObjectURL(blob)
  try {
    const anchor = document.createElement('a')
    anchor.href = url
    anchor.download = filename
    anchor.rel = 'noopener'
    document.body.appendChild(anchor)
    anchor.click()
    anchor.remove()
  } finally {
    // Give the browser a tick to start the download before revoking.
    setTimeout(() => URL.revokeObjectURL(url), 1000)
  }
}
